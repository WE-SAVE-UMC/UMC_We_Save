package com.example.we_save.ui.main.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.we_save.R
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.databinding.FragmentAccidentBinding
import com.example.we_save.ui.accident.AccidentEditorFragment
import com.example.we_save.ui.main.pages.accident.NearMeFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class AccidentFragment : Fragment() {
    private var _binding: FragmentAccidentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccidentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> viewPager.currentItem = 0
                        1 -> viewPager.currentItem = 1
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    with(tabLayout) {
                        selectTab(
                            when (position) {
                                0 -> getTabAt(0)!!
                                1 -> getTabAt(1)!!
                                else -> return
                            }
                        )
                    }
                }
            })

            viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

            registerButton.setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.findFragmentByTag("accident_editor") != null) return@setOnClickListener

                fragmentManager.commit {
                    setAppAnimation()
                    replace(R.id.fragment_container, AccidentEditorFragment(), "accident_editor")
                    addToBackStack(null)
                }
            }
        }
    }

    private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NearMeFragment()
                else -> NearMeFragment()
            }
        }
    }
}