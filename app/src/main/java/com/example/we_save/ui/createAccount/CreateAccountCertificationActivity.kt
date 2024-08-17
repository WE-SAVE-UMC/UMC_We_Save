package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.data.apiservice.CheckSmsRequest
import com.example.we_save.data.apiservice.CheckSmsResponse
import com.example.we_save.R
import com.example.we_save.SplashActivity
import com.example.we_save.data.apiservice.SmsRequest
import com.example.we_save.databinding.ActivityCreateAccountCertificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAccountCertificationActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateAccountCertificationBinding
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startTimer()

        val loginbutton: Button = binding.certificationButtonTv
        val errormessage: TextView = binding.certificationErrorMessageTv
        val certificationnum: EditText = binding.enterCertificationNumber
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val phoneNum = intent.getStringExtra("phoneNum") ?: ""

        loginbutton.setOnClickListener {
            val text = certificationnum.text.toString()
            if (text.isNotEmpty()) {
                val checkRequest = CheckSmsRequest(phoneNum, text)
                checkSmsCode(checkRequest)
            } else {
                errormessage.visibility = TextView.VISIBLE
                certificationnum.setBackgroundResource(R.drawable.edittext_error_background)
                errormessage.text = "인증번호를 입력해주세요."
            }
        }
        // 재요청 버튼
        binding.reRequestTv.setOnClickListener {
            requestSmsCode(phoneNum)
        }
        // 엔터를 눌렀을때 자동으로 넘어간다.!!
        binding.enterCertificationNumber.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                val text = certificationnum.text.toString()
                if (text.isNotEmpty()) {
                    val checkRequest = CheckSmsRequest(phoneNum, text)
                    checkSmsCode(checkRequest)
                } else {
                    errormessage.visibility = TextView.VISIBLE
                    certificationnum.setBackgroundResource(R.drawable.edittext_error_background)
                    errormessage.text = "인증번호를 입력해주세요."
                }
                true
            } else {
                false
            }
        }
    }

    private fun checkSmsCode(checkRequest: CheckSmsRequest) {
        val call = SplashActivity.RetrofitInstance.smsapi.checkSms(checkRequest)
        call.enqueue(object : Callback<CheckSmsResponse> {
            override fun onResponse(call: Call<CheckSmsResponse>, response: Response<CheckSmsResponse>) {
                if (response.isSuccessful && response.body()?.result?.valid == true) {
                    val intent = Intent(this@CreateAccountCertificationActivity, CreateAccountPasswordActivity::class.java)
                    intent.putExtra("phoneNum", checkRequest.phoneNum)
                    startActivity(intent)
                } else {
                    binding.certificationErrorMessageTv.visibility = TextView.VISIBLE
                    binding.enterCertificationNumber.setBackgroundResource(R.drawable.edittext_error_background)
                    binding.certificationErrorMessageTv.text = response.body()?.result?.message ?: "인증번호가 유효하지 않습니다."
                }
            }

            override fun onFailure(call: Call<CheckSmsResponse>, t: Throwable) {
                Toast.makeText(this@CreateAccountCertificationActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.timerTv.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTv.text = "00:00"
            }
        }.start()
    }
    private fun requestSmsCode(phoneNum: String) {  // 인증번호 보내는 함수
        val smsRequest = SmsRequest(phoneNum)
        val call = SplashActivity.RetrofitInstance.smsapi.requestSms(smsRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateAccountCertificationActivity, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this@CreateAccountCertificationActivity, "인증번호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CreateAccountCertificationActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}