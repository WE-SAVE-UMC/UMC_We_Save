package com.example.we_save.ui.my

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.databinding.ActivitySettingBlockBinding

class SettingBlockActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingBlockBinding.inflate(layoutInflater)
        var intent: Intent
        setContentView(binding.root)

        // 뒤로가기 버튼 (차단관리 -> 설정)
        binding.toolbar.setOnClickListener {
            intent = Intent(this, MySettingActivity::class.java)
            startActivity(intent)
        }

    }
}