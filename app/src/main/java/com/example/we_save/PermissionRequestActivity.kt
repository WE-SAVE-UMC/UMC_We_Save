package com.example.we_save

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.databinding.ActivityPermissionRequestBinding

class PermissionRequestActivity : AppCompatActivity() {
    lateinit var binding : ActivityPermissionRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val textView = binding.agreementTv

        val text = "앱 실행을 위해서 설정 - 애플리케이션 관리자 - WE SAVE - 권한에서 권한을 허용해주시기 바랍니다.\n필수 권한을 허용하지 않으면 WE SAVE 를 이용하실 수 없습니다.\n휴대폰 SMS 본인 인증시 인증번호 6자리가 채워질 수 있습니다."

        // SpannableString을 생성합니다.
        val spannableString = SpannableString(text)

        // 줄 바꿈 부분의 인덱스를 찾습니다.
        val firstLineBreakIndex = text.indexOf('\n')
        val secondLineBreakIndex = text.indexOf('\n', firstLineBreakIndex + 1)

        // 첫 번째 줄 바꿈 이후 부분에 줄 간격을 설정합니다.
        spannableString.setSpan(
            CustomLineHeightSpan(32), // 32dp의 줄 간격을 설정합니다.
            firstLineBreakIndex + 1, // 첫 번째 줄 바꿈 이후의 시작 인덱스
            secondLineBreakIndex, // 두 번째 줄 바꿈까지의 인덱스
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            CustomLineHeightSpan(32), // 32dp의 줄 간격을 설정합니다.
            secondLineBreakIndex + 1, // 두 번째 줄 바꿈 이후의 시작 인덱스
            text.length, // 텍스트의 끝까지
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
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
                startMainActivity()
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

    companion object {
        private const val REQUEST_CODE_MULTIPLE_PERMISSIONS = 100
    }
}