package com.example.we_save

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.databinding.FragmentSearchDescriptionBinding

class SearchDescriptionFragment : Fragment() {
    lateinit var binding: FragmentSearchDescriptionBinding
    private var isDescriptionVisible = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.underArrowIv.setOnClickListener {
            if (isDescriptionVisible) {
                binding.descriptionContainer.visibility = View.GONE
                binding.underArrowIv.setImageResource(R.drawable.under_arrow_ic)
            } else {
                binding.descriptionContainer.visibility = View.VISIBLE
                binding.underArrowIv.setImageResource(R.drawable.up_arrow)
            }
            isDescriptionVisible = !isDescriptionVisible
        }
    }
}
