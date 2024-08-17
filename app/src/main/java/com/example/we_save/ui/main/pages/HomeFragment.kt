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
import android.widget.Toolbar
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.we_save.ui.alarm.AdvertiseMentActivity
import com.example.we_save.ui.alarm.AlarmActivity
import com.example.we_save.R
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
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings

class HomeFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var selectedButton: MaterialCardView? = null
    private lateinit var mapView: MapView
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
        naverMap.uiSettings.isZoomControlEnabled = false
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

            // 지도 설정
            val fm = childFragmentManager
            val mapFragment = fm.findFragmentById(R.id.map_view) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction().add(R.id.map_view, it).commit()
                }


            mapFragment.getMapAsync(this)

        }


        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black)
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()) ?: 0
        hideToolbar()
        binding.quizBackground.setOnClickListener {
            val intent = Intent(requireContext(), AdvertiseMentActivity::class.java)
            startActivity(intent)
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
            replaceFragment(MainDistanceFragment())
        }

        binding.okFilterButton1.setOnClickListener {
            selectButton(it as MaterialCardView, R.id.ok_filter_tv)
            replaceFragment(MainDistanceFragment())
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()

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
    private fun updateTabStyle(tab: TabLayout.Tab?, isSelected: Boolean) {
        tab?.let {
            val tabView = it.view
            if (isSelected) {
                tabView.background = ContextCompat.getDrawable(requireContext(), R.drawable.main_tab_selected_background)
                val tabText = findTextViewInTab(tabView)
                tabText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                tabView.background = ContextCompat.getDrawable(requireContext(), R.drawable.main_tab_unselected_background)
                val tabText = findTextViewInTab(tabView)
                tabText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_40))
            }
        }
    }
    private fun hideToolbar() {
        // Toolbar 숨기기
        safelyUpdateToolbarVisibility(View.GONE)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        activity?.findViewById<AppBarLayout>(R.id.appBarLayout)?.visibility = View.GONE
    }

    private fun showToolbar() {
        // Toolbar 보이기
        safelyUpdateToolbarVisibility(View.VISIBLE)
    }
    private fun safelyUpdateToolbarVisibility(visibility: Int) {
        activity?.let {
            if (!it.isFinishing) {
                it.findViewById<MaterialToolbar>(R.id.toolbar1)?.visibility = visibility
            }
        }
    }
}