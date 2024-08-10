package com.example.we_save.ui.createAccount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.data.apiservice.LoginRequest
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.R
import com.example.we_save.SplashActivity
import com.example.we_save.databinding.ActivityLoginBinding
import com.example.we_save.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

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

        loginButton.setOnClickListener {
            val phoneNum = phoneNumber.text.toString()
            val passwordText = password.text.toString()

            if (phoneNum.isNotEmpty() && passwordText.isNotEmpty()) {
                val loginRequest = LoginRequest(phoneNum, passwordText)
                loginUser(loginRequest)
            } else {
                Toast.makeText(this, "전화번호와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        createAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(loginRequest: LoginRequest) {
        val call = SplashActivity.RetrofitInstance.api.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.isSuccess) {
                        // 토큰을 저장!!
                        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("jwtToken", loginResponse.result.token)
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