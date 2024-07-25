package com.example.we_save.ui.main.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.databinding.FragmentFacilitiesBinding

class FacilitiesFragment : Fragment() {
    private var _binding: FragmentFacilitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacilitiesBinding.inflate(inflater, container, false)
        return binding.root
    }
}