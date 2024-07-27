package com.example.we_save

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.databinding.ActivityCreateAccountCertificationBinding

class CreateAccountCertificationActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateAccountCertificationBinding
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startTimer()
        val loginbutton : Button = binding.certificationButtonTv
        val errormessage : TextView = binding.certificationErrorMessageTv
        val certificationnum : EditText = binding.enterCertificationNumber

        loginbutton.setOnClickListener {
            val text = certificationnum.text
            //조건문
            //일치한다면
            val intent = Intent(this,CreateAccountPasswordActivity::class.java)
            startActivity(intent)
            /*//일치하지 않는다면
            errormessage.visibility = TextView.VISIBLE
            certificationnum.setBackgroundColor(Color.RED)
            errormessage.text = "인증번호를 다시 확인해주세요."*/
        }
    }
    private fun startTimer() {   //타이머 기능
        countDownTimer = object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.timerTv.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTv.text = "00:00"
                // 타이머가 끝났을때
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel() // 끌때 타이머 초기화
    }
}