package com.example.we_save.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.we_save.R
import com.example.we_save.databinding.FragmentMainBinding
import com.example.we_save.ui.main.pages.AccidentFragment
import com.example.we_save.ui.main.pages.FacilitiesFragment
import com.example.we_save.ui.main.pages.HomeFragment
import com.example.we_save.ui.main.pages.MyFragment

class MainFragment : Fragment() {
    // View 바인딩 변수를 선언
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // FragmentMainBinding을 사용하여 레이아웃을 인플레이트
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // bottomNavigationView의 패딩을 조정하여 하단 패딩을 0으로 설정
            bottomNavigationView.setOnApplyWindowInsetsListener { view, insets ->
                view.updatePadding(bottom = 0)
                insets
            }

            // ViewPager2의 페이지 변경 콜백을 설정
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 페이지 변경 시 bottomNavigationView의 선택된 아이템을 동기화
                    bottomNavigationView.selectedItemId = when (position) {
                        0 -> R.id.action_home
                        1 -> R.id.action_facilities
                        2 -> R.id.action_accident
                        3 -> R.id.action_my
                        else -> return
                    }
                }
            })

            // bottomNavigationView의 아이템 선택 리스너를 설정
            bottomNavigationView.setOnItemSelectedListener {
                // 선택된 아이템에 따라 ViewPager2의 현재 아이템을 변경
                viewPager.currentItem = when (it.itemId) {
                    R.id.action_home -> 0
                    R.id.action_facilities -> 1
                    R.id.action_accident -> 2
                    R.id.action_my -> 3
                    else -> return@setOnItemSelectedListener false
                }
                return@setOnItemSelectedListener true
            }

            // 사용자가 직접 페이지를 스와이프하지 못하도록 설정
            viewPager.isUserInputEnabled = false
            // ViewPager2의 어댑터를 설정
            viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

            // 인텐트로 전달된 인자를 받아서 ViewPager2의 현재 아이템 설정
            val selectedPage = arguments?.getInt("selectedPage") ?: 0
            viewPager.currentItem = selectedPage


        }
    }

    // ViewPager2 어댑터 클래스 정의
    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        // 페이지 수 반환
        override fun getItemCount() = 4

        // 포지션에 따라 해당 프래그먼트를 생성하여 반환
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()  // TODO : 이름 변경
                1 -> FacilitiesFragment()   //TODO : 이름 변경
                2 -> AccidentFragment()
                else -> MyFragment() //TODO : 이름 변경
            }
        }
    }
    fun setViewPagerPage(page: Int) {  //view pager 에 있는 프래그먼트 간의 이동을 위한 함수
        binding.viewPager.currentItem = page
    }
    fun navigateToAccidentFragmentAndSelectDomestic() {
        binding.viewPager.currentItem = 2

        // AccidentFragment의 ViewPager를 DomesticFragment로 설정
        val accidentFragment = childFragmentManager.findFragmentByTag("f1") as? AccidentFragment
        accidentFragment?.setViewPagerPage(1)
    }
}
