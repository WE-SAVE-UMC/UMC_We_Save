package com.example.we_save

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.databinding.ActivityCreateAccountBinding

class CreateAccountActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginButtonTv.setOnClickListener {
            val intent = Intent(this,CreateAccountCertificationActivity::class.java)
            startActivity(intent)
        }
    }
}