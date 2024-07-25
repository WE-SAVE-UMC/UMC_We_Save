package com.example.we_save.ui.accident.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.we_save.R
import com.example.we_save.databinding.BottomSheetYourAccidentBinding
import com.example.we_save.ui.accident.AccidentBottomSheetEvent
import com.example.we_save.ui.accident.DATA_KEY_EVENT
import com.example.we_save.ui.accident.REQUEST_KEY_BOTTOM_SHEET_EVENT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class YourAccidentBottomSheet :
    BottomSheetDialogFragment(R.layout.bottom_sheet_your_accident) {
    override fun getTheme(): Int {
        return R.style.App_Widget_RoundedBackgroundBottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetYourAccidentBinding.bind(view)
        with(binding) {
            reportButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.REPORT)
                dismiss()
            }

            shareButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.SHARE)
                dismiss()
            }

            blockButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.BLOCK)
                dismiss()
            }
        }
    }

    private fun setResult(event: AccidentBottomSheetEvent) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY_BOTTOM_SHEET_EVENT,
            bundleOf(DATA_KEY_EVENT to event.ordinal)
        )
    }
}
