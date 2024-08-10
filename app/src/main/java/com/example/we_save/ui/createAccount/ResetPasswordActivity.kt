package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity(), BottomSheetAgencyFragment.OnOptionSelectedListener{

    lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetFragment = BottomSheetAgencyFragment()


        // 통신사 선택 bottom sheet
        binding.resetPasswordAgencyTv.setOnClickListener {
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        // 인증번호 입력 화면으로 이동
        binding.resetPasswordBtn.setOnClickListener {
            val intent = Intent(this, ResetPasswordCertificationActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onOptionSelected(option: String) {
        // 선택된 옵션을 처리
        binding.resetPasswordAgencyTv.text = option
        binding.resetPasswordAgencyTv.setTextColor(ContextCompat.getColor(this, R.color.gray_80))
    }

}