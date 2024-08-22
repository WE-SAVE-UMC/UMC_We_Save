package com.example.we_save.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.we_save.R
import com.example.we_save.databinding.ActivityMainBinding
import com.example.we_save.ui.main.MainFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)

        supportFragmentManager.commit {
            replace(R.id.fragment_container, MainFragment(), "root")
        }
        val targetPage = intent.getIntExtra("TARGET_PAGE", 0)

        // MainFragment에 인자 전달
        val fragment = MainFragment().apply {
            arguments = Bundle().apply {
                putInt("selectedPage", targetPage)
            }
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        with(binding) {
            val badgeDrawable = BadgeDrawable.create(this@MainActivity).apply {
                backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.red_10)
            }

            // Badge 추가하는 방법 (삭제는 BadgeUtils.detachBadgeDrawable() 호출)
            BadgeUtils.attachBadgeDrawable(badgeDrawable, toolbar1, R.id.action_notification)
        }

        lifecycleScope.launch {
            // Location preload
            viewModel.address.collectLatest {}
        }
    }

}