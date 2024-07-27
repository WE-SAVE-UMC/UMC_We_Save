package com.example.we_save

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val loginbutton : Button = binding.loginButtonTv
        val errormessage : TextView = binding.loginErrorMessage
        val phonenumber : EditText = binding.phonenumberEdittext
        val password : EditText = binding.passwordEdittext
        val createAccount : TextView = binding.createAccountTv

        loginbutton.setOnClickListener {
            val phonetext = phonenumber.text
            val passwordtext = password.text
            //조건문
            //일치하지 않는다면
            errormessage.visibility = TextView.VISIBLE
            phonenumber.setBackgroundColor(Color.RED) //edtitext 창이 빨간색으로 변한다.
            password.setBackgroundColor(Color.RED)
            errormessage.text = "전화번호 혹은 비밀번호가 올바르지 않습니다."
            loginbutton.setBackgroundColor(ContextCompat.getColor(this, Color.GRAY)) // 버튼 색깔을 변경한단.
        }
        createAccount.setOnClickListener {
            //val intent = Intent(this,CreateAccountActivity::class.java)
            startActivity(intent)
        }

    }
}