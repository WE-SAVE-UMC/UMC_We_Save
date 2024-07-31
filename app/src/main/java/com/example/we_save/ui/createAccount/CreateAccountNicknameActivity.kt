package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.databinding.ActivityCreateAccountNicknameBinding

class CreateAccountNicknameActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateAccountNicknameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val passwordbutton : Button = binding.nicknameButtonTv
        val nicknameerrormessage : TextView = binding.nicknameErrorMessageTv
        val nicknamesuccessmessage : TextView = binding.nicknameSuccessMessageTv
        val nickname : EditText = binding.enterNicknameNumber

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        passwordbutton.setOnClickListener {
            val nicknametext = nickname.text
            //일치한다면
            nicknamesuccessmessage.visibility = TextView.VISIBLE
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            //일치하지 않는다면
            /*nicknameerrormessage.visibility = TextView.VISIBLE
            nickname.setBackgroundColor(Color.RED)
            passworderrormessage.text = "전화번호 혹은 비밀번호가 올바르지 않습니다."*/
        }
    }
}