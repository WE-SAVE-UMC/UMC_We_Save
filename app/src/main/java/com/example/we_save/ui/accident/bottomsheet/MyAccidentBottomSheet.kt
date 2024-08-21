package com.example.we_save.ui.accident.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.example.we_save.R
import com.example.we_save.data.model.PostDetails
import com.example.we_save.databinding.BottomSheetMyAccidentBinding
import com.example.we_save.ui.accident.AccidentBottomSheetEvent
import com.example.we_save.ui.accident.DATA_KEY_EVENT
import com.example.we_save.ui.accident.REQUEST_KEY_BOTTOM_SHEET_EVENT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MyAccidentBottomSheet :
    BottomSheetDialogFragment(R.layout.bottom_sheet_my_accident) {
    private var details: PostDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            details = requireArguments().getParcelable("details")
        }

        if (savedInstanceState != null) {
            details = savedInstanceState.getParcelable("details")
        }
    }

    override fun getTheme(): Int {
        return R.style.App_Widget_RoundedBackgroundBottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetMyAccidentBinding.bind(view)
        with(binding) {
            completeContainer.isVisible = details?.status != "COMPLETED"

            situationEndButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.SITUATION_END)
                dismiss()
            }

            editButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.EDIT)
                dismiss()
            }

            removeButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.REMOVE)
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
