package com.example.we_save.ui.createAccount

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.example.we_save.R
import com.example.we_save.databinding.BottomSheetAgencyBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.map.NaverMap.OnOptionChangeListener

class BottomSheetAgencyFragment : BottomSheetDialogFragment() {

    interface OnOptionSelectedListener {
        fun onOptionSelected(option: String)
    }

    private var listener: OnOptionSelectedListener? = null
    private var _binding: BottomSheetAgencyBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnOptionSelectedListener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetAgencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.agencyRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            // 모든 라디오 버튼을 기본 상태로 재설정
            for (i in 0 until group.childCount) {
                val button = group.getChildAt(i) as RadioButton
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_radio_btn_off, 0)
            }
            // 선택된 라디오 버튼의 상태를 선택된 상태로 설정
            val selectedButton = group.findViewById<RadioButton>(checkedId)
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_radio_btn_on, 0)

            // 선택된 버튼의 텍스트를 전달
            listener?.onOptionSelected(selectedButton.text.toString())
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}