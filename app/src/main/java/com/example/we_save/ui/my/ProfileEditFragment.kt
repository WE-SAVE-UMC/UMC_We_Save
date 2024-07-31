package com.example.we_save.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.R
import com.example.we_save.databinding.FragmentMyPostBinding
import com.example.we_save.databinding.FragmentProfileEditBinding
import com.example.we_save.ui.main.pages.MyFragment

class ProfileEditFragment : Fragment() {

    lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)


        return binding.root
    }


}
