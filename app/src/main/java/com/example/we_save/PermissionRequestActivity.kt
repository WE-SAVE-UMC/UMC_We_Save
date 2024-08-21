package com.example.we_save

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.common.extensions.customToast
import com.example.we_save.databinding.ActivityPermissionRequestBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.createAccount.LoginActivity

class PermissionRequestActivity : AppCompatActivity() {
    lateinit var binding: ActivityPermissionRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태바 색상 및 스타일 설정
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 권한 요청 버튼 클릭 리스너 설정
        binding.agreeSignUpTv.setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        // 요청할 권한 목록
        val permissions = mutableListOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        }*/

        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
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
                customToast("데이터가 없습니다")
                finish()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // 서버로부터 사용자의 로그인 여부 확인
    private fun isUserLoggedIn(): Boolean {
        // 예시로 로그인이 되어 있지 않다고 가정
        return false
    }

    companion object {
        private const val REQUEST_CODE_MULTIPLE_PERMISSIONS = 100
    }
}