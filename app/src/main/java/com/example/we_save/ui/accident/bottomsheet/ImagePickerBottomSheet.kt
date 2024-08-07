package com.example.we_save.ui.accident.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.we_save.R
import com.example.we_save.databinding.BottomSheetImagePickerBinding
import com.example.we_save.ui.accident.AccidentBottomSheetEvent
import com.example.we_save.ui.accident.DATA_KEY_EVENT
import com.example.we_save.ui.accident.REQUEST_KEY_BOTTOM_SHEET_EVENT
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ImagePickerBottomSheet :
    BottomSheetDialogFragment(R.layout.bottom_sheet_image_picker) {
    override fun getTheme(): Int {
        return R.style.App_Widget_TransparentBackgroundBottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val padding = arguments?.getInt("padding")

        val binding = BottomSheetImagePickerBinding.bind(view)
        with(binding) {
            if (padding != null) {
                root.setPadding(0, 0, 0, padding.toInt())
            }

            root.setOnClickListener {
                dismiss()
            }

            albumButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.ALBUM)
                dismiss()
            }

            cameraButton.setOnClickListener {
                setResult(AccidentBottomSheetEvent.CAMERA)
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
