package com.example.we_save.ui.createAccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.R
import com.example.we_save.data.apiservice.LoginRequest
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.data.apiservice.ProfileInterface
import com.example.we_save.data.apiservice.ProfileResponse
import com.example.we_save.data.apiservice.ResetPasswordInterface
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.ActivityEnterPasswordBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.my.ZeroTransformationMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnterPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityEnterPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enterPasswordEt.transformationMethod = ZeroTransformationMethod()


        // 확인 버튼 -> 새로운 비밀번호 입력 화면
        binding.enterPasswordOkBtn.setOnClickListener {
            checkPassword(binding.enterPasswordEt.text.toString())
        }

        binding.enterPasswordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 실행되는 부분

                // 텍스트가 입력되면 버튼이 빨간색으로 바뀜
                binding.enterPasswordOkBtn.setBackgroundResource(R.drawable.red_button)
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후
            }
        })

    }

    private fun checkPassword(password: String){
        val spf = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val phoneNum = spf.getString("phoneNum", "00000000000")

        // 입력된 비밀번호를 통해 loginRequest 객체 생성
        val loginRequest = phoneNum?.let { LoginRequest(it, password) }

        val resetPasswordService = getRetrofit().create(ResetPasswordInterface::class.java)

        if (loginRequest != null) {
            resetPasswordService.checkPassword(loginRequest).enqueue(object: Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    Log.d("checkPassword/SUCCESS", response.toString())

                    if (response.isSuccessful) {
                        val resp = response.body()

                        if (resp != null && resp.isSuccess) {   // 비밀번호가 맞았을 경우
                            val intent = Intent(
                                this@EnterPasswordActivity,
                                ResetPasswordEnterActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            binding.enterPasswordEt.setBackgroundResource(R.drawable.edittext_error_background)
                            binding.enterPasswordIncorrectTv.visibility = View.VISIBLE
                            // 입력된 비밀번호 삭제
                            binding.enterPasswordEt.setText("")
                        }
                    } else {
                        binding.enterPasswordEt.setBackgroundResource(R.drawable.edittext_error_background)
                        binding.enterPasswordIncorrectTv.visibility = View.VISIBLE
                        // 입력된 비밀번호 삭제
                        binding.enterPasswordEt.setText("")
                    }



                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("checkPassword/FAIL",t.message.toString())
                }

            })
        }


    }
}


