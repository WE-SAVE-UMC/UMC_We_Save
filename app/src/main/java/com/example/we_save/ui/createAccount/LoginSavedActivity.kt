package com.example.we_save.ui.createAccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.databinding.ActivityLoginSavedBinding

class LoginSavedActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginSavedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spf = this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val phoneNum = spf.getString("phoneNum", "00000000000").toString()
        val formattedPhoneNum = phoneNum?.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")

        // 직전에 로그인 했던 전화번호를 띄움
        binding.phoneTv.text = formattedPhoneNum

        // x 버튼 -> login 액티비티로 이동
        binding.xBtn.setOnClickListener {
            // 로그아웃
            logout()

            // 로그인 액티비티로 전환
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.phoneTv.setOnClickListener {
            // 로그인 액티비티로 전환
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("phoneNum", phoneNum)
            startActivity(intent)
        }
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
}