package com.example.we_save.ui.accident

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.we_save.databinding.FragmentAccidentTypeBinding
import com.example.we_save.domain.model.AccidentType

class AccidentTypeFragment : Fragment() {
    private var _binding: FragmentAccidentTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccidentTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            fireButton.setOnClickListener { finish(AccidentType.FIRE) }
            earthquakeButton.setOnClickListener { finish(AccidentType.EARTHQUAKE) }
            heavyRainButton.setOnClickListener { finish(AccidentType.HEAVY_RAIN) }
            heavySnowButton.setOnClickListener { finish(AccidentType.HEAVY_SNOW) }
            trafficButton.setOnClickListener { finish(AccidentType.TRAFFIC) }
            etcButton.setOnClickListener { finish(AccidentType.ETC) }
        }
    }

    private fun finish(type: AccidentType) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY_ACCIDENT_TYPE, bundleOf(
                DATA_KEY_TYPE to type.ordinal
            )
        )

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}