package com.example.we_save.ui.accident

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.we_save.R
import com.example.we_save.databinding.FragmentAccidentTypeBinding
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.ui.alarm.AlarmActivity

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
            logoBar.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_notification) {
                    startActivity(Intent(requireContext(), AlarmActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })
                }

                return@setOnMenuItemClickListener true
            }

            backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            fireButton.setOnClickListener { finish(AccidentType.FIRE) }
            earthquakeButton.setOnClickListener { finish(AccidentType.EARTHQUAKE) }
            heavyRainButton.setOnClickListener { finish(AccidentType.HEAVY_RAIN) }
            heavySnowButton.setOnClickListener { finish(AccidentType.HEAVY_SNOW) }
            trafficButton.setOnClickListener { finish(AccidentType.TRAFFIC_ACCIDENT) }
            etcButton.setOnClickListener { finish(AccidentType.OTHER) }
        }
    }

    private fun finish(type: AccidentType) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY_CATEGORY, bundleOf(
                DATA_KEY_TYPE to type.ordinal
            )
        )

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}