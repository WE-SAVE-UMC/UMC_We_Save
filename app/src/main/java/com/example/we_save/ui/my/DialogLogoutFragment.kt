package com.example.we_save.ui.my

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.example.we_save.R
import com.example.we_save.databinding.DialogLogoutBinding
import com.example.we_save.databinding.FragmentMyBinding
import com.example.we_save.ui.createAccount.LoginActivity
import com.example.we_save.ui.createAccount.LoginSavedActivity

class DialogLogoutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_logout, null)
        dialog.setContentView(dialogView)

        // 로그인된 전화번호
        val numberTextView: TextView = dialogView.findViewById(R.id.dialog_logout_num_tv)
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val phoneNum = spf.getString("phoneNum", "00000000000")
        val formattedPhoneNum = phoneNum?.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")

        numberTextView.text = formattedPhoneNum


        val cancelButton: ConstraintLayout = dialogView.findViewById(R.id.dialog_logout_cancel)
        val okButton: ConstraintLayout = dialogView.findViewById(R.id.dialog_logout_ok)

        // 취소 버튼
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // 확인 버튼
        okButton.setOnClickListener {
            dialog.dismiss()

            // 로그아웃, 로그인 saved 화면으로 이동
            val intent = Intent(requireContext(), LoginSavedActivity::class.java)
            startActivity(intent)
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