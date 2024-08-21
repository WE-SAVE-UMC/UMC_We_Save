package com.example.we_save.ui.createAccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.R
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.ResetPasswordInterface
import com.example.we_save.data.apiservice.ResetPasswordRequest
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.ActivityResetPasswordEnterBinding
import com.example.we_save.ui.my.ZeroTransformationMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordEnterActivity : AppCompatActivity() {

    lateinit var binding: ActivityResetPasswordEnterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 비밀번호 입력 EditText에 ZeroTransformationMethod 적용
        binding.resetPasswordEnterEt.transformationMethod = ZeroTransformationMethod()
        binding.resetPasswordEnter2Et.transformationMethod = ZeroTransformationMethod()


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
                            // 비밀번호 재설정
                            resetPassword(password2)

                            logout()
                            // 로그인 화면으로 전환
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

    private fun resetPassword(newPassword: String){
        val resetPasswordService = getRetrofit().create(ResetPasswordInterface::class.java)

        val spf = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val phoneNum = spf.getString("phoneNum", "00000000000").toString()

        val request = ResetPasswordRequest(phoneNum, newPassword)

        resetPasswordService.resetPassword(request).enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("PATCHPassword/SUCCESS", response.toString())
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("PATCHPassword/FAIL", t.message.toString())
            }

        })
    }

    private fun logout(){
        val spf = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = spf.edit()
        // 사용자 토큰 삭제
        editor.remove("jwtToken")
        editor.remove("phoneNum")
        editor.apply()

        // 로그인 화면으로 이동
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }

}