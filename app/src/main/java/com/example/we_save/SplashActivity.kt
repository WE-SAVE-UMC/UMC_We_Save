package com.example.we_save

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.databinding.ActivitySplashBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.createAccount.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplashBinding

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한이 있는지 확인하고 다음 액티비티로 전환
        if (hasAllPermissions()) {
            Handler(Looper.getMainLooper()).postDelayed({
                // 모든 권한이 허용된 경우 메인 액티비티 또는 로그인 액티비티로 이동
                navigateToNextActivity()
            }, 2000) // 2초 동안 스플래쉬 화면을 표시합니다.
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, PermissionRequestActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)

        }
    }

    private fun hasAllPermissions(): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 서버로부터 사용자의 로그인 여부 확인
    private fun isUserLoggedIn(): Boolean {
        //val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        //return sharedPreferences.getBoolean("login", false)
        return false
    }

    private fun navigateToNextActivity() {
        if (isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}