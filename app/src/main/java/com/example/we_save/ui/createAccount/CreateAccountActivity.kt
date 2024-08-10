package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.data.apiservice.PhoneNumberResponse
import com.example.we_save.R
import com.example.we_save.data.apiservice.SmsRequest
import com.example.we_save.SplashActivity
import com.example.we_save.databinding.ActivityCreateAccountBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccountActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.loginButtonTv.setOnClickListener {
            val phoneNum = binding.enterPhoneNumber.text.toString()
            if (phoneNum.isNotEmpty()) {
                checkPhoneNumberValidity(phoneNum)
            } else {
                Toast.makeText(this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPhoneNumberValidity(phoneNum: String) { // 전화번호 유효성 검사하는 함수
        val call = SplashActivity.RetrofitInstance.validapi.checkPhoneNumber(phoneNum)
        call.enqueue(object : Callback<PhoneNumberResponse> {
            override fun onResponse(call: Call<PhoneNumberResponse>, response: Response<PhoneNumberResponse>) {
                if (response.isSuccessful && response.body()?.result?.isValid == true) {
                    requestSmsCode(phoneNum)
                } else {
                    Toast.makeText(this@CreateAccountActivity, "중복된 전화번호 입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PhoneNumberResponse>, t: Throwable) {
                Toast.makeText(this@CreateAccountActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun requestSmsCode(phoneNum: String) {  // 인증번호 보내는 함수
        val smsRequest = SmsRequest(phoneNum)
        val call = SplashActivity.RetrofitInstance.smsapi.requestSms(smsRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateAccountActivity, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                    // 인증번호 전송이 성공하면 인증번호 입력 화면으로 이동
                    val intent = Intent(this@CreateAccountActivity, CreateAccountCertificationActivity::class.java)
                    intent.putExtra("phoneNum", phoneNum)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CreateAccountActivity, "인증번호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CreateAccountActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}