package com.example.we_save.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.databinding.FragmentMySettingAreaBinding

class MySettingAreaFragment : Fragment() {

    lateinit var binding: FragmentMySettingAreaBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySettingAreaBinding.inflate(inflater, container, false)
        val intent = Intent(context, HeaderActivity::class.java)




        return binding.root
    }
}