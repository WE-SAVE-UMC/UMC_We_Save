package com.example.we_save.ui.createAccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.R
import com.example.we_save.databinding.ActivityCreateAccountNameBinding

class CreateAccountNameActivity : AppCompatActivity() {
    lateinit var binding : ActivityCreateAccountNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountNameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.nameButtonTv.setOnClickListener {
            val intent = Intent(this,CreateAccountActivity::class.java)
            startActivity(intent)
        }
        // 엔터를 눌렀을때 자동으로 넘어간다.!!
        binding.nameEdittext.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                val intent = Intent(this, CreateAccountActivity::class.java)
                startActivity(intent)
                true
            } else {
                false
            }
        }

    }
}