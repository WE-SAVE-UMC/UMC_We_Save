package com.example.we_save.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.databinding.FragmentMySettingUserBinding
import com.example.we_save.ui.createAccount.EnterPasswordActivity

class MySettingUserFragment : Fragment() {

    lateinit var binding: FragmentMySettingUserBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySettingUserBinding.inflate(inflater, container, false)
        val intent = Intent(context, HeaderActivity::class.java)

        // 로그아웃 -> 로그아웃 다이얼로그 표시
        binding.settingUserLogout.setOnClickListener {
            showCustomDialog()
        }

        // 탈퇴하기 -> 탈퇴 시 주의사항
        binding.settingUserWithdrawTv.setOnClickListener {
            intent.putExtra("nextPage", "MySettingUserWithdrawFragment")
            startActivity(intent)
        }

        // 비밀번호 재설정 -> 비밀번호 재설정 화면
        val intentEnterPassword = Intent(context, EnterPasswordActivity::class.java)
        binding.settingUserPassword.setOnClickListener {
            startActivity(intentEnterPassword)
        }

        return binding.root
    }


    private fun showCustomDialog(){
        val customDialogFragment = DialogLogoutFragment()
        customDialogFragment.show(parentFragmentManager, "customDialog")
    }
}