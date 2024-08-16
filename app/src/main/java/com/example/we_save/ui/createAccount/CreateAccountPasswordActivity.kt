package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.data.apiservice.CheckSmsRequest
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
        // 엔터를 눌렀을때 자동으로 넘어간다.!!
        binding.enterPasswordNumber.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.reenterPasswordNumber.requestFocus()
                true
            } else {
                false
            }
        }
        binding.reenterPasswordNumber.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val passwordtext = binding.enterPasswordNumber.text.toString()
                val reenterPassword = binding.reenterPasswordNumber.text.toString()

                if (passwordtext == reenterPassword) {
                    // 비밀번호가 일치하면 닉네임 설정 화면으로 이동
                    Log.d("CreateAccountPasswordActivity", "Passwords match, proceeding to nickname screen")
                    val intent = Intent(this, CreateAccountNicknameActivity::class.java)
                    intent.putExtra("phoneNum", phoneNum)
                    intent.putExtra("password", passwordtext)
                    startActivity(intent)
                } else {
                    // 비밀번호가 일치하지 않으면 에러 메시지 표시
                    passworderrormessage.visibility = TextView.VISIBLE
                    repassword.setBackgroundResource(R.drawable.edittext_error_background)
                    passworderrormessage.text = "전화번호 혹은 비밀번호가 올바르지 않습니다."
                }
                true
            } else {
                false
            }
        }

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