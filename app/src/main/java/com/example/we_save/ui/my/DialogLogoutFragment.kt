package com.example.we_save.ui.my

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.we_save.R

class DialogLogoutFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)
        dialog.setContentView(dialogView)

        val cancelButton: ConstraintLayout = dialogView.findViewById(R.id.dialog_logout_cancel)
        val okButton: ConstraintLayout = dialogView.findViewById(R.id.dialog_logout_ok)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        okButton.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "확인 버튼이 클릭되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 다이얼로그 크기 설정
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // 배경의 어두운 정도
        dialog?.window?.setDimAmount(0.6f)

        // 다이얼로그의 기본 배경을 제거함
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}