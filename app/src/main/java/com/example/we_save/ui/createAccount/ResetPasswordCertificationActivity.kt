package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.R
import com.example.we_save.databinding.ActivityResetPasswordCertificationBinding

class ResetPasswordCertificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityResetPasswordCertificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // 확인 버튼 동작 -> 비밀번호 재설정 페이지
        binding.resetPasswordCertificationOkBtn.setOnClickListener {
            val intent = Intent(this, ResetPasswordEnterActivity::class.java)
            startActivity(intent)
        }

//        // 인증번호 예시
//        val answer: Int = 1234
//
//        // 사용자가 입력한 인증번호
//        val num: Int = binding.resetPasswordCertificationEt.getText().toString().toInt()
//
//
//        if (answer != num) {
//            binding.resetPasswordCertificationEt.setBackgroundResource(R.drawable.shape_edittext_red_box)
//            binding.resetPasswordCertificationIncorrectTv.visibility = View.VISIBLE
//        } else {
//            binding.resetPasswordCertificationEt.setBackgroundResource(R.drawable.shape_edittext_gray_box)
//            binding.resetPasswordCertificationIncorrectTv.visibility = View.INVISIBLE
//
//
//        }


    }


}