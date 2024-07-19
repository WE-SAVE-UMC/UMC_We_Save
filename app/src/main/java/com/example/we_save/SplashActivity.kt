package com.example.we_save

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    lateinit var binding : ActivitySplashBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // 권한이 있는지 확인하고 다음 액티비티로 전환
            Handler(Looper.getMainLooper()).postDelayed({
                if (hasAllPermissions()) {
                    // 모든 권한이 허용된 경우 메인 액티비티로 이동
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // 권한이 허용되지 않은 경우 권한 요청 액티비티로 이동
                    startActivity(Intent(this, PermissionRequestActivity::class.java))
                }
                finish()
            }, 2000) // 2초 동안 스플래쉬 화면을 표시합니다.
        }

        private fun hasAllPermissions(): Boolean {
            val permissions = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )

            return permissions.all { permission ->
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            }
        }
}