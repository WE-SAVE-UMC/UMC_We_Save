package com.example.we_save.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val tabTitles = arrayOf("거리순", "최신순", "확인순")

    override fun getItemCount(): Int {
        return tabTitles.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainDistanceFragment()
            1 -> MainDistanceFragment()
            2 -> MainDistanceFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
