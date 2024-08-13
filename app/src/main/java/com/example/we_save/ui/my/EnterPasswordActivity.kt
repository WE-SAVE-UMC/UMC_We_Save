package com.example.we_save.ui.my

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.databinding.ActivityEnterPasswordBinding
import com.example.we_save.ui.createAccount.ResetPasswordActivity
import com.example.we_save.ui.createAccount.ResetPasswordEnterActivity

class EnterPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityEnterPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enterPasswordFindBtn.setOnClickListener {
            val intentFind = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intentFind)
        }

        binding.enterPasswordOkBtn.setOnClickListener {
            val intentReset = Intent(this, ResetPasswordEnterActivity::class.java)
            startActivity(intentReset)
        }

    }
}


