package com.example.we_save.ui.createAccount

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.SplashActivity
import com.example.we_save.common.Constants
import com.example.we_save.data.apiservice.LoginRequest
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.databinding.ActivityLoginBinding
import com.example.we_save.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton: Button = binding.loginButtonTv
        val errorMessage: TextView = binding.loginErrorMessage
        val phoneNumber: EditText = binding.phonenumberEdittext
        val password: EditText = binding.passwordEdittext
        val createAccount: TextView = binding.createAccountTv

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        binding.phonenumberEdittext.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.passwordEdittext.requestFocus()
                true
            } else {
                false
            }
        }
        binding.passwordEdittext.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                var phoneNum = phoneNumber.text.toString()
                val passwordText = password.text.toString()

                if (phoneNum.isNotEmpty() && passwordText.isNotEmpty()) {
                    phoneNum = removeHyphens(phoneNum) // 서버로 넘기기 전에 하이픈 제거
                    val loginRequest = LoginRequest(phoneNum, passwordText)
                    loginUser(loginRequest)
                } else {
                    Toast.makeText(this, "전화번호와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }

        // 전화번호 입력 시 하이픈 추가
        phoneNumber.addTextChangedListener(object : TextWatcher {
            private var isEditing: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return

                isEditing = true

                val formatted = formatPhoneNumber(s.toString())
                phoneNumber.setText(formatted)
                phoneNumber.setSelection(formatted.length) // 커서를 맨 끝으로 이동

                isEditing = false
            }
        })

        loginButton.setOnClickListener {
            var phoneNum = phoneNumber.text.toString()
            val passwordText = password.text.toString()

            if (phoneNum.isNotEmpty() && passwordText.isNotEmpty()) {
                phoneNum = removeHyphens(phoneNum) // 서버로 넘기기 전에 하이픈 제거
                val loginRequest = LoginRequest(phoneNum, passwordText)
                loginUser(loginRequest)
            } else {
                Toast.makeText(this, "전화번호와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        createAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountNameActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 -> 비밀번호 재설정 화면
        val intentResetPassword = Intent(this, EnterPasswordActivity::class.java)
        binding.forgotPasswordTv.setOnClickListener {
            startActivity(intentResetPassword)
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        val digits = phone.replace("-", "")
        val length = digits.length

        return when {
            length >= 8 -> "${digits.substring(0, 3)}-${
                digits.substring(
                    3,
                    7
                )
            }-${digits.substring(7)}"

            length >= 4 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
            else -> digits
        }
    }

    private fun removeHyphens(phone: String): String {
        return phone.replace("-", "")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun loginUser(loginRequest: LoginRequest) {
        val call = SplashActivity.RetrofitInstance.api.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.isSuccess) {
                        val token = loginResponse.result.token
                        Log.d("loginActivity", "Token: $token")  // 여기서 토큰 확인하셔도 됩니다
                        val sharedPreferences =
                            getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("jwtToken", loginResponse.result.token)
                        editor.putLong(Constants.KEY_USER_ID, loginResponse.result.userId.toLong())
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showError("전화번호 혹은 비밀번호가 올바르지 않습니다.")
                    }
                } else {
                    showError("전화번호 혹은 비밀번호가 올바르지 않습니다.")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showError("네트워크 오류: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        binding.loginErrorMessage.apply {
            visibility = View.VISIBLE
            text = message
        }
        binding.phonenumberEdittext.setBackgroundResource(R.drawable.edittext_error_background)
        binding.passwordEdittext.setBackgroundResource(R.drawable.edittext_error_background)


    }
}