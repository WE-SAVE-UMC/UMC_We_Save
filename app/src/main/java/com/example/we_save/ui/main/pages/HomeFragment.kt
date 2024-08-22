package com.example.we_save.ui.main.pages

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.we_save.MainCheckFragment
import com.example.we_save.MainRecentFragment
import com.example.we_save.ui.alarm.AdvertiseMentActivity
import com.example.we_save.ui.alarm.AlarmActivity
import com.example.we_save.R
import com.example.we_save.data.apiservice.HomeRequest
import com.example.we_save.data.apiservice.HomeResponse
import com.example.we_save.data.apiservice.HomeService
import com.example.we_save.data.apiservice.HostPostDto
import com.example.we_save.data.apiservice.RetrofitClient
import com.example.we_save.ui.search.SearchActivity
import com.example.we_save.databinding.FragmentHomeBinding
import com.example.we_save.ui.main.MainDistanceFragment
import com.example.we_save.ui.main.MainFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import android.location.Location
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.view.MotionEvent
import com.example.we_save.common.extensions.customToast
import com.example.we_save.data.apiservice.GetQuizResponse
import com.example.we_save.data.apiservice.QuizResult
import com.naver.maps.map.CameraAnimation

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedButton: MaterialCardView? = null
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markers = mutableListOf<Marker>()
    private var quizId: Int? = null  // quizId를 저장할 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)
        requireActivity().window.decorView.systemUiVisibility = 0 //
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)



        binding.myLocationImage.setOnClickListener {
            getCurrentLocation()
        }

        binding.mapStarIcon.setOnClickListener {

            val sharedPreferences = requireContext().getSharedPreferences("region", Context.MODE_PRIVATE)
            val regionName = sharedPreferences.getString("regionname", "서울특별시 강남구")
            Log.d("HomeFragment", "Retrieved regionName: $regionName")

            moveToRegion(regionName ?: "서울특별시 강남구")
        }
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                bottomNavigationView?.visibility = View.GONE
            } else {
                bottomNavigationView?.visibility = View.VISIBLE
            }
        }

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.uiSettings.isZoomControlEnabled = false

        // 지도에 마커 추가
        val regionName = "서울특별시 노원구 월계동"
        val southKoreaBounds = LatLngBounds(
            LatLng(33.0, 125.0),
            LatLng(38.5, 129.5)
        )

        // 카메라를 대한민국 전체가 보이도록 설정
        val cameraUpdate = CameraUpdate.fitBounds(southKoreaBounds, 100)
        naverMap.moveCamera(cameraUpdate)

        fetchHomeData()
    }

    private fun addMarkerToMap(latitude: Double, longitude: Double, regionName: String, categoryName: String, post: HostPostDto) {
        val marker = Marker()
        marker.position = LatLng(latitude, longitude)

        val iconResource = when (categoryName) {
            "화재" -> R.drawable.map_fire_marker_icon
            "지진" -> R.drawable.map_earthquake_marker_icon
            "교통사고" -> R.drawable.map_car_marker_icon
            "폭우" -> R.drawable.map_rain_marker_icon
            "폭설" -> R.drawable.ic_snowflake_16
            else -> R.drawable.ic_etc_16
        }

        marker.icon = OverlayImage.fromResource(iconResource)
        marker.map = naverMap

        // 마커에 주소값 저장

        marker.tag = regionName
        Log.d("HomeFragment", "Marker tag set to: ${marker.tag}")

        markers.add(marker)

        marker.setOnClickListener {
            updateUI(post)
            true
        }
    }
    private fun moveToRegion(targetRegion: String) {
        val targetRegionShort = targetRegion.split(" ").take(2).joinToString(" ")

        markers.find { marker ->
            val tag = marker.tag as? String
            tag?.contains(targetRegionShort) == true
        }?.let { marker ->
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 15.0)
                .animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)
        } ?: Log.d("HomeFragment", "확인: $targetRegionShort")
    }

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalBadgeUtils::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        val toolbar = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    // Menu items have been created, we can now find our item
                    val menuItem = menu.findItem(R.id.action_notification)
                    val badgeDrawable = BadgeDrawable.create(requireContext())
                    BadgeUtils.detachBadgeDrawable(
                        badgeDrawable,
                        toolbar,
                        menuItem.itemId
                    )
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return false
                }
            }, viewLifecycleOwner)

            toolbar.visibility = View.GONE
        }

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()) ?: 0
        hideToolbar()
        binding.quizBackground.setOnClickListener {
            onQuizClicked()
        }
        val alarmImageView = view.findViewById<ImageView>(R.id.main_alarm_iv)
        val whitealarmImageView = view.findViewById<ImageView>(R.id.white_alarm_iv)

        // 클릭 리스너 설정
        alarmImageView.setOnClickListener {
            // 다른 액티비티로 이동
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }
        whitealarmImageView.setOnClickListener {
            // 다른 액티비티로 이동
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }

        binding.searchIv.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                binding.upperEdittext, // 전환할 뷰
                "shared_edittext" // transitionName
            )
            startActivity(intent, options.toBundle())
        }
        val mainFragment = parentFragment as? MainFragment
        binding.koreaConstlay.setOnClickListener {

            val sharedPreferences = requireActivity().getSharedPreferences("Move", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putInt("pageToSet", 1)  // 1번 페이지로 이동 (DomesticFragment)
                apply()
            }

            mainFragment?.setViewPagerPage(2)

        }
        binding.nearAccidentFrame.setOnClickListener {

            val sharedPreferences = requireActivity().getSharedPreferences("Move", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putInt("pageToSet", 0)  // 0번 페이지로 이동 (DomesticFragment)
                apply()
            }

            mainFragment?.setViewPagerPage(2)

        }

        val darkToolbar = binding.maintoolbar
        val whiteToolbar = binding.maintoolbar1
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.main_nestscrollview)

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val viewHeight = v.height
            val totalHeight = v.getChildAt(0).measuredHeight
            val threshold = 620 // 끝에서 100dp 전에 배경을 변경
            Log.d("ScrollDebug", "Scroll Y: $scrollY, View Height: $viewHeight, Total Height: $totalHeight")
            if (scrollY + viewHeight >= totalHeight - threshold) {  // 스크롤이 끝에 도달했을 때
                darkToolbar.toolbar1.visibility = View.GONE
                whiteToolbar.toolbar2.visibility = View.VISIBLE
            } else {
                Log.d("ScrollDebug", "Hiding custom background")
                // 스크롤이 끝에 도달하지 않았을 때, 원래 배경으로 돌릴 수 있습니다.
                darkToolbar.toolbar1.visibility = View.VISIBLE
                whiteToolbar.toolbar2.visibility = View.GONE
            }
        })
        // 초기 프래그먼트를 일단 거리순으로 설정
        if (savedInstanceState == null) {
            replaceFragment(MainDistanceFragment())
            selectButton(binding.distanceFilterButton, R.id.distance_filter_tv)
        }

        binding.distanceFilterButton.setOnClickListener {
            selectButton(it as MaterialCardView, R.id.distance_filter_tv)
            replaceFragment(MainDistanceFragment())
        }

        binding.recentFilterButton1.setOnClickListener {
            selectButton(it as MaterialCardView, R.id.recent_filter_tv)
            replaceFragment(MainRecentFragment())
        }

        binding.okFilterButton1.setOnClickListener {
            selectButton(it as MaterialCardView, R.id.ok_filter_tv)
            replaceFragment(MainCheckFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectButton(button: MaterialCardView, textViewId: Int) {
        selectedButton?.apply {
            // 선택 해제된 버튼의 배경 및 텍스트 색상 복구
            setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toggle_filter_background))
            findViewById<TextView>(R.id.distance_filter_tv)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_40))
            findViewById<TextView>(R.id.recent_filter_tv)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_40))
            findViewById<TextView>(R.id.ok_filter_tv)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_40))
            strokeColor = ContextCompat.getColor(requireContext(), R.color.toggle_filter_stroke) // 기본 테두리 색상
        }

        button.apply {
            // 선택된 버튼의 배경 및 텍스트 색상 설정
            setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_50))
            findViewById<TextView>(textViewId).setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            strokeColor = ContextCompat.getColor(requireContext(), android.R.color.transparent) // 테두리 색상 투명으로 설정
        }

        selectedButton = button
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }

    private fun findTextViewInTab(tabView: ViewGroup): TextView? {
        for (i in 0 until tabView.childCount) {
            val child = tabView.getChildAt(i)
            if (child is TextView) {
                return child
            } else if (child is ViewGroup) {
                val textView = findTextViewInTab(child)
                if (textView != null) {
                    return textView
                }
            }
        }
        return null
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 부여되지 않은 경우 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1000
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                moveCameraToLocation(currentLatLng)
            } else {

                requireContext().applicationContext.customToast("현재 본인 위치를 찾을 수 없습니다!!")
            }
        }
    }

    private fun moveCameraToLocation(latLng: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Easing)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun hideToolbar() {
        // Toolbar 숨기기
        safelyUpdateToolbarVisibility(View.GONE)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
    }

    private fun safelyUpdateToolbarVisibility(visibility: Int) {
        activity?.let {
            if (!it.isFinishing) {
                it.findViewById<MaterialToolbar>(R.id.toolbar1)?.visibility = visibility
            }
        }
    }

    // hostDto
    private fun fetchHomeData() {
        val homeService = RetrofitClient.createService(HomeService::class.java)

        // 실제 리퀘스트 데이터를 이곳에 넣습니다.
        val request = HomeRequest(latitude = 25.0, longitude = 50.0, regionName = "서울특별시 노원구 월계동")

        homeService.getHomeData(request).enqueue(object : Callback<HomeResponse> {
            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.isSuccessful) {
                    val homeResponse = response.body()

                    homeResponse?.result?.let { result ->
                        quizId = result.quizId
                        binding.quizQustion.text = result.quizText
                        val hotPostDtos = result.hostPostDtos

                        if (!hotPostDtos.isNullOrEmpty()) {
                            // 서버에서 받은 데이터로 마커 추가
                            for (post in hotPostDtos) {
                                addMarkerToMap(post.latitude, post.longitude, post.regionName, post.categoryName, post)
                            }
                        } else {
                            requireContext().applicationContext.customToast("데이터가 없습니다")
                        }
                    } ?: run {
                        requireContext().applicationContext.customToast("서버 응답이 없습니다")
                    }
                } else {
                    requireContext().applicationContext.customToast("서버에 오류가 생겼습니다")
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                requireContext().applicationContext.customToast("네트워크에 오류가 생겼습니다")
            }
        })
    }


    private fun updateUI(post: HostPostDto) {
        binding.mapWhiteBackground.visibility = View.VISIBLE  // UI 요소를 나타냄
        binding.mapAccidentTv.text = post.title
        val createAtWithPadding = post.createAt.padEnd(26, '0')
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val dateTime = LocalDateTime.parse(createAtWithPadding, formatter)
        val timePart = dateTime.format(DateTimeFormatter.ofPattern("HH시 mm분"))
        binding.subMapAccidentTv.text = timePart
        binding.mapOkNumberTv.text = post.hearts.toString()
        binding.mapDistanceTv.text = String.format("%.1fkm", post.distance / 1000)
        binding.mapRegionNameTv.text = post.regionName
        binding.mapCategoryTv.text = post.categoryName + "발생"

        // 이미지가 있을 경우 로드
        if (post.imageUrl != null) {
            Glide.with(this)
                .load("http://114.108.153.82/${post.imageUrl}")  // 지금 이 부분이 이상함
                .into(binding.mainMapImageIv)
        } else {
            // 이미지가 없을 때 기본 이미지 설정
            //binding.mainMapImageIv.setImageResource(R.drawable.main_map_image_icon)
        }


    }
    private fun onQuizClicked() {
        quizId?.let {
            val intent = Intent(requireContext(), AdvertiseMentActivity::class.java)
            intent.putExtra("quizId", it)
            startActivity(intent)
        } ?: run {
            Toast.makeText(requireContext(), "퀴즈 데이터를 로드하지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
