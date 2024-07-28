package com.example.we_save.ui.my

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.R
import com.example.we_save.databinding.ActivityMySettingBinding
import com.example.we_save.ui.main.pages.MyFragment

class MySettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityMySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.settingBackIv.setOnClickListener {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.main, MyFragment())
//                .commitAllowingStateLoss()
//        }
        // 뒤로가기 버튼 (설정 -> 마이페이지)
        binding.toolbar.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.view_pager, MyFragment())
                .commitAllowingStateLoss()
        }


    }
}