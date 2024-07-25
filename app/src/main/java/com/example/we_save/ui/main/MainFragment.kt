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
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            bottomNavigationView.setOnApplyWindowInsetsListener { view, insets ->
                view.updatePadding(bottom = 0)
                insets
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    bottomNavigationView.selectedItemId = when (position) {
                        0 -> R.id.action_home
                        1 -> R.id.action_facilities
                        2 -> R.id.action_accident
                        3 -> R.id.action_my
                        else -> return
                    }
                }
            })

            bottomNavigationView.setOnItemSelectedListener {
                viewPager.currentItem = when (it.itemId) {
                    R.id.action_home -> 0
                    R.id.action_facilities -> 1
                    R.id.action_accident -> 2
                    R.id.action_my -> 3
                    else -> return@setOnItemSelectedListener false
                }

                return@setOnItemSelectedListener true
            }

            viewPager.isUserInputEnabled = false
            viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        }
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> FacilitiesFragment()
                2 -> AccidentFragment()
                else -> MyFragment()
            }
        }
    }
}