package com.example.we_save

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.Manifest
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.databinding.ActivityPermissionRequestBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.createAccount.LoginActivity

class PermissionRequestActivity : AppCompatActivity() {
    lateinit var binding : ActivityPermissionRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val textView = binding.agreementTv


        binding.agreeSignUpTv.setOnClickListener {
            requestPermissions()
        }
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_CODE_MULTIPLE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 모든 권한이 허용된 경우
                if (isUserLoggedIn()) {
                    startMainActivity()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            } else {
                // 권한이 하나라도 거부된 경우
                Toast.makeText(this, "모든 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    // 서버로 부터 사용자의 로그인 유뮤 확인
    private fun isUserLoggedIn(): Boolean {
        //val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        //return sharedPreferences.getBoolean("isLoggedIn", false)
        return false
    }

    companion object {
        private const val REQUEST_CODE_MULTIPLE_PERMISSIONS = 100
    }
}