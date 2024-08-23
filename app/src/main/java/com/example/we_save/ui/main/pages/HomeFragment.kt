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
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.lifecycleScope
import com.example.we_save.common.extensions.customToast
import com.example.we_save.data.apiservice.GetQuizResponse
import com.example.we_save.data.apiservice.QuizResult
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedButton: MaterialCardView? = null
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markers = mutableListOf<Marker>()
    private var quizId: Int? = null  // quizId를 저장할 변수

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        // 초기 프래그먼트를 거리순으로 설정
        if (savedInstanceState == null) {
           // resetButtons() // 다른 모든 버튼을 기본 상태로 초기화
            replaceFragment(MainDistanceFragment())
            selectButton(binding.distanceFilterButton, R.id.distance_filter_tv)
        }
        binding.mapView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    binding.mainNestscrollview.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.mainNestscrollview.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }


        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)
        requireActivity().window.decorView.systemUiVisibility = 0 //
        mapView = view.findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 위치 소스 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 내 위치 버튼 클릭 리스너
        binding.myLocationImage.setOnClickListener {
            binding.myLocationImage.setImageResource(R.drawable.click_map_mylocation_icon)
            locationSource.lastLocation?.let { location ->
                // 위치로 스크롤하고 줌 레벨을 설정하는 CameraUpdate 생성
                val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(location.latitude, location.longitude), 15.0)
                    .animate(CameraAnimation.Easing)  // 애니메이션 적용

                // 카메라를 해당 위치로 이동하고 줌 레벨 적용
                naverMap.moveCamera(cameraUpdate)

                // 5초 후에 위치 추적 모드 비활성화
                Handler(Looper.getMainLooper()).postDelayed({
                    naverMap.locationTrackingMode = LocationTrackingMode.None
                }, 5000)
            } ?: requireContext().applicationContext.customToast("현재 위치를 가져올 수 없습니다.")

            Handler(Looper.getMainLooper()).postDelayed({
                binding.myLocationImage.setImageResource(R.drawable.map_mylocation_icon)
            }, 1000)
        }


        binding.mapStarIcon.setOnClickListener {

            val sharedPreferences = requireContext().getSharedPreferences("region", Context.MODE_PRIVATE)
            val regionName = sharedPreferences.getString("regionname","")
            Log.d("HomeFragment", "Retrieved regionName: $regionName")
            if (regionName.isNullOrEmpty()) {
                Log.d("HomeFragment", "regionName이 설정되지 않았습니다. 아무 작업도 하지 않습니다.")
                return@setOnClickListener // 값을 넘겨받지 못하면 아무 작업도 하지 않음
            }
            moveToRegion(regionName)

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
        val gestureDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                binding.mainNestscrollview.requestDisallowInterceptTouchEvent(true)
                return false
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                binding.mainNestscrollview.requestDisallowInterceptTouchEvent(false)
                return false
            }
        })

        binding.mapView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isZoomControlEnabled = false
        setupMap()
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
    private fun setupMap() {
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        locationSource.lastLocation?.let {
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude))
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.d("YourFragment", "isLocationEnabled: $isEnabled")
        return isEnabled
    }

    private fun addMarkerToMap(latitude: Double, longitude: Double, regionName: String, categoryName: String, post: HostPostDto) {
        val marker = Marker()
        marker.position = LatLng(latitude, longitude)

        val iconResource = when (categoryName) {
            "화재" -> R.drawable.map_fire_marker_icon
            "지진" -> R.drawable.map_earthquake_marker_icon
            "교통사고" -> R.drawable.map_car_marker_icon
            "폭우" -> R.drawable.map_rain_marker_icon
            "폭설" -> R.drawable.map_snowflake_marker
            else -> R.drawable.map_etc_marker_icon
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
        binding.mapStarIcon.setImageResource(R.drawable.click_map_star_icon)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.mapStarIcon.setImageResource(R.drawable.main_map_star_icon)
        }, 1000)
        val targetRegionShort = targetRegion.split(" ").take(2).joinToString(" ")

        val marker = markers.find { marker ->
            val tag = marker.tag as? String
            tag?.contains(targetRegionShort) == true
        }

        if (marker != null) {
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(marker.position, 15.0)
                .animate(CameraAnimation.Easing)
            naverMap.moveCamera(cameraUpdate)

            // 5초 후에 위치 추적 모드를 비활성화
            Handler(Looper.getMainLooper()).postDelayed({
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }, 5000)
        } else {
            Log.d("HomeFragment", "Marker not found for region: $targetRegionShort")
        }
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

        binding.upperEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.endsWith(" ")) {
                        binding.upperEdittext.removeTextChangedListener(this) // 잠시 리스너 제거
                        binding.upperEdittext.setText(it.trim()) // 공백 제거
                        binding.upperEdittext.setSelection(binding.upperEdittext.text.length) // 커서 위치 조정
                        binding.upperEdittext.addTextChangedListener(this) // 리스너 다시 추가
                    }
                }
            }
        })

        binding.upperEdittext.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.upperEdittext.text.toString().trim()
                if (query.isNotEmpty()) {
                    val sharedPreferences = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("search_query", query)
                    editor.apply()

                    // 이후 다른 프래그먼트나 액티비티로 이동
                    val intent = Intent(context, SearchActivity::class.java)
                    startActivity(intent)
                }
                return@setOnEditorActionListener true // 이벤트를 소비하여 줄바꿈 방지
            }
            return@setOnEditorActionListener false
        }

        binding.searchIv.setOnClickListener {
            val query = binding.upperEdittext.text.toString().trim()
            if (query.isNotEmpty()) {
                val sharedPreferences = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("search_query", query)
                editor.apply()

                val intent = Intent(context, SearchActivity::class.java)
                startActivity(intent)
            }
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
        val request = HomeRequest(
            latitude = 25.0,
            longitude = 50.0,
            regionName = "서울특별시 강남구 역삼동"
        )

        homeService.getHomeData(request).enqueue(object : Callback<HomeResponse> {
            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.isSuccessful) {
                    val homeResponse = response.body()

                    homeResponse?.result?.let { result ->
                        quizId = result.quizId
                        binding.quizQustion.text = result.quizText
                        val hotPostDtos = result.hostPostDtos
                        val PostDtos = result.postDtos
                        val hostPostTitles = result.hostPostDtos.map { it.title }
                        val postTitles = result.postDtos.map{it.title}
                        val postRegionName = result.postDtos.map{it.regionName.substringBefore("동") + "동"}
                        if (!hotPostDtos.isNullOrEmpty()) {
                            // 서버에서 받은 데이터로 마커 추가
                            updateTextViewWithTitles(hostPostTitles)
                            for (post in hotPostDtos) {
                                addMarkerToMap(post.latitude, post.longitude, post.regionName, post.categoryName, post)
                            }
                        } else {

                        }
                        if (!PostDtos.isNullOrEmpty()) {
                            updateTextViewWithTitles2(postRegionName)
                            updateTextViewWithTitles1(postTitles)

                        } else {
                        }
                    } ?: run {
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
            }
        })
    }
    private fun updateTextViewWithTitles(titles: List<String>) {
        viewLifecycleOwner.lifecycleScope.launch {
            var currentTitleIndex = 0
            while (isActive) {  // Coroutine이 종료될 때까지 반복
                val binding = this@HomeFragment.binding ?: break  // binding이 null인지 확인 후 반복 종료
                binding.mapAlarmTv.text = titles[currentTitleIndex]
                currentTitleIndex = (currentTitleIndex + 1) % titles.size
                delay(5000L) // 5초 딜레이
            }
        }
    }
    private fun updateTextViewWithTitles1(titles: List<String>) {
        viewLifecycleOwner.lifecycleScope.launch {
            var currentTitleIndex = 0
            while (isActive) {
                val binding = this@HomeFragment.binding ?: break
                binding.textView2.text = titles[currentTitleIndex]
                currentTitleIndex = (currentTitleIndex + 1) % titles.size
                delay(5000L) // 5초 딜레이
            }
        }
    }

    private fun updateTextViewWithTitles2(regionName: List<String>) {
        viewLifecycleOwner.lifecycleScope.launch {
            var currentTitleIndex = 0
            while (isActive) {
                val binding = this@HomeFragment.binding ?: break
                binding.mainUpperText.text = regionName[currentTitleIndex]
                currentTitleIndex = (currentTitleIndex + 1) % regionName.size
                delay(5000L) // 5초 딜레이
            }
        }
    }


    private fun updateUI(post: HostPostDto) {
        binding.mapWhiteBackground.visibility = View.VISIBLE  // UI 요소를 나타냄
        val maxLength = 12  // 최대 허용 길이
        val title = if (post.title.length > maxLength) {
            post.title.substring(0, maxLength) + " ..."
        } else {
            post.title
        }

        binding.mapAccidentTv.text = title
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

        }
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
