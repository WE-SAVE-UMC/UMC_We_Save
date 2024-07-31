package com.example.we_save.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.we_save.ui.alarm.AdvertiseMentActivity
import com.example.we_save.ui.alarm.AlarmActivity
import com.example.we_save.ui.main.MainTabAdapter
import com.example.we_save.R
import com.example.we_save.SearchActivity
import com.example.we_save.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
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


        tabLayout = binding.nearAccidentTablayout
        viewPager = binding.nearAccidentViewPager

        val tabAdapter = MainTabAdapter(requireActivity())
        viewPager.adapter = tabAdapter

        // TabLayout과 ViewPager2를 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = arrayOf("거리순", "최신순", "확인순")[position]
        }.attach()

        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(2, 0, 2, 0)
            tab.requestLayout()
        }

        tabLayout.getTabAt(0)?.select()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = tab.view
                tabView.background = ContextCompat.getDrawable(requireContext(), R.drawable.main_tab_selected_background)
                val tabText = findTextViewInTab(tabView)
                tabText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = tab.view
                tabView.background = ContextCompat.getDrawable(requireContext(), R.drawable.main_tab_unselected_background)
                val tabText = findTextViewInTab(tabView)
                tabText?.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_40))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        updateTabStyle(tabLayout.getTabAt(0), true)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        showToolbar()
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