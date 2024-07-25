package com.example.we_save

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.databinding.ActivitySplashBinding
import com.example.we_save.ui.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    private val permissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.values.any { !it }) {
                finish()
                return@registerForActivityResult
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 권한이 있는지 확인하고 다음 액티비티로 전환
        if (hasAllPermissions()) {
            Handler(Looper.getMainLooper()).postDelayed({
                // 모든 권한이 허용된 경우 메인 액티비티로 이동
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000) // 2초 동안 스플래쉬 화면을 표시합니다.
        } else {
            // 권한이 허용되지 않은 경우 권한 요청 액티비티로 이동
            requestPermissions.launch(permissions)
        }
    }

    private fun hasAllPermissions(): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}