package com.example.we_save.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.R
import com.example.we_save.databinding.FragmentMySettingAreaBinding
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback

class MySettingAreaFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMySettingAreaBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMySettingAreaBinding.inflate(inflater, container, false)

        // Initialize the map
        val mapFragment = childFragmentManager.findFragmentById(R.id.my_setting_area_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.my_setting_area_map_view, it).commit()
            }
        mapFragment.getMapAsync(this)

        // Start HeaderActivity with an intent
        val intent = Intent(context, HeaderActivity::class.java)


        return binding.root
    }

    override fun onMapReady(naverMap: NaverMap) {
        // Customize the map here (e.g., setting the initial position, UI settings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}