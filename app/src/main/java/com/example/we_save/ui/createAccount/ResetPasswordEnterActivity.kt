package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.R
import com.example.we_save.databinding.ActivityResetPasswordEnterBinding

class ResetPasswordEnterActivity : AppCompatActivity() {

    lateinit var binding: ActivityResetPasswordEnterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 비밀번호 재입력 시 실행되는 부분
        binding.resetPasswordEnter2Et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 실행되는 부분
                val password = binding.resetPasswordEnterEt.text.toString()
                val password2 = s.toString()  // 직접 전달된 텍스트 사용

                if (password2.isNotEmpty()) {  // password2에 값이 입력된 경우에만 실행
                    if (password != password2) {
                        binding.resetPasswordEnterIncorrectTv.visibility = View.VISIBLE
                        binding.resetPasswordEnterOkBtn.setBackgroundResource(R.drawable.gray_button)
                    } else {
                        binding.resetPasswordEnterIncorrectTv.visibility = View.INVISIBLE
                        binding.resetPasswordEnterOkBtn.setBackgroundResource(R.drawable.red_button)

                        // 비밀번호 재설정 버튼 -> 로그인 화면
                        binding.resetPasswordEnterOkBtn.setOnClickListener {
                            val intent = Intent(this@ResetPasswordEnterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후
            }
        })







    }

}