package com.example.we_save.ui.my

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.we_save.R
import com.example.we_save.databinding.ActivityProfileEditBinding
import com.example.we_save.ui.main.pages.MyFragment

class ProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.profileEditBackIv.setOnClickListener {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.main, MyFragment())
//                .commitAllowingStateLoss()
//        }
        // 뒤로가기 버튼 (프로필 설정 -> 마이페이지)
        binding.toolbar.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.view_pager, MyFragment())
                .commitAllowingStateLoss()
        }

    }

}
