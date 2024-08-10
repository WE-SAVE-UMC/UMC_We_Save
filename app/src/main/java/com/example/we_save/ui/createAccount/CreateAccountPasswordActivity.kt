package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.databinding.ActivityCreateAccountPassworBinding

class CreateAccountPasswordActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateAccountPassworBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountPassworBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val passwordbutton : Button = binding.passwordButtonTv
        val passworderrormessage : TextView = binding.passwordErrorMessageTv
        val password : EditText = binding.enterPasswordNumber
        val repassword : EditText = binding.reenterPasswordNumber

        val phoneNum = intent.getStringExtra("phoneNum") ?: ""
        Log.d("CreateAccountPasswordActivity", "Received phoneNum: $phoneNum")

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        passwordbutton.setOnClickListener {
            val passwordtext = password.text.toString()
            val repasswordtext = repassword.text.toString()
            //조건문
            if(passwordtext==repasswordtext){
                Log.d("CreateAccountPasswordActivity", "Passwords match, proceeding to nickname screen")
                val intent = Intent(this, CreateAccountNicknameActivity::class.java)
                intent.putExtra("phoneNum", phoneNum)
                intent.putExtra("password", passwordtext)
                startActivity(intent)
            }
            else{
                //일치하지 않는다면
                passworderrormessage.visibility = TextView.VISIBLE
                password.setBackgroundResource(R.drawable.edittext_error_background)
                repassword.setBackgroundResource(R.drawable.edittext_error_background)
                passworderrormessage.text = "전화번호 혹은 비밀번호가 올바르지 않습니다."
            }

        }
    }
}