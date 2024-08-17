package com.example.we_save.ui.createAccount

import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.data.apiservice.NicknameResponse
import com.example.we_save.R
import com.example.we_save.SplashActivity
import com.example.we_save.User
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import com.example.we_save.databinding.ActivityCreateAccountNicknameBinding

class CreateAccountNicknameActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateAccountNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val passwordButton: Button = binding.nicknameButtonTv
        val nicknameErrorMessage: TextView = binding.nicknameErrorMessageTv
        val nicknameSuccessMessage: TextView = binding.nicknameSuccessMessageTv
        val nickname: EditText = binding.enterNicknameNumber

        val phoneNum = intent.getStringExtra("phoneNum") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        Log.d("CreateAccountNicknameActivity", "Received phoneNum: $phoneNum, password: $password")

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        passwordButton.setOnClickListener {
            val nicknameText = nickname.text.toString()
            if (nicknameText.isNotEmpty()) {
                val userRequest = User(phoneNum, password, nicknameText)
                Log.d("CreateAccountNicknameActivity", "Creating user with: $userRequest")
                checkNicknameValidity(nicknameText, phoneNum, password)
            } else {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.enterNicknameNumber.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                val nicknameText = nickname.text.toString()
                if (nicknameText.isNotEmpty()) {
                    val userRequest = User(phoneNum, password, nicknameText)
                    Log.d("CreateAccountNicknameActivity", "Creating user with: $userRequest")
                    checkNicknameValidity(nicknameText, phoneNum, password)
                } else {
                    Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
        binding.enterNicknameNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후 사용됩니다.
                val nicknameText = s.toString().trim()
                if (nicknameText.isNotEmpty()) {
                    realtimecheckNicknameValidity(nicknameText)
                } else {
                    binding.nicknameSuccessMessageTv.visibility = TextView.GONE
                    binding.nicknameErrorMessageTv.visibility = TextView.GONE
                }
            }
        })

        }

    private fun checkNicknameValidity(nickname: String, phoneNum: String, password: String) {// 닉네임 유효성 체크
        val call = SplashActivity.RetrofitInstance.validapi.checkNickname(nickname)
        call.enqueue(object : Callback<NicknameResponse> {
            override fun onResponse(call: Call<NicknameResponse>, response: Response<NicknameResponse>) {
                if (response.isSuccessful && response.body()?.result?.isValid == true) {
                    Toast.makeText(this@CreateAccountNicknameActivity, "유효한 닉네임입니다.", Toast.LENGTH_SHORT).show()
                    val userRequest = User(phoneNum, password, nickname)
                    createUser(userRequest)
                } else {
                    Toast.makeText(this@CreateAccountNicknameActivity, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NicknameResponse>, t: Throwable) {
                Toast.makeText(this@CreateAccountNicknameActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun realtimecheckNicknameValidity(nickname: String) {
        val call = SplashActivity.RetrofitInstance.validapi.checkNickname(nickname)
        call.enqueue(object : Callback<NicknameResponse> {
            override fun onResponse(call: Call<NicknameResponse>, response: Response<NicknameResponse>) {
                if (response.isSuccessful && response.body()?.result?.isValid == true) {
                    // 닉네임 사용 가능
                    binding.nicknameSuccessMessageTv.visibility = TextView.VISIBLE
                    binding.nicknameErrorMessageTv.visibility = TextView.GONE
                    binding.nicknameSuccessMessageTv.text = "사용 가능한 닉네임 입니다."
                } else {
                    // 닉네임 사용 불가능
                    binding.nicknameErrorMessageTv.visibility = TextView.VISIBLE
                    binding.nicknameSuccessMessageTv.visibility = TextView.GONE
                    binding.nicknameErrorMessageTv.text = "사용불가능한 닉네임 입니다."
                }
            }

            override fun onFailure(call: Call<NicknameResponse>, t: Throwable) {
                // 네트워크 오류 처리
                Toast.makeText(this@CreateAccountNicknameActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
                binding.nicknameErrorMessageTv.visibility = TextView.VISIBLE
                binding.nicknameSuccessMessageTv.visibility = TextView.GONE
                binding.nicknameErrorMessageTv.text = "네트워크 오류로 닉네임 검사를 완료할 수 없습니다."
            }
        })
    }

    private fun createUser(userRequest: User) {
        val call = SplashActivity.RetrofitInstance.api.createUser(userRequest)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("CreateAccountNicknameActivity", "회원가입 성공: ${response.body()}")
                    Toast.makeText(this@CreateAccountNicknameActivity, "회원가입 성공", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@CreateAccountNicknameActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("CreateAccountNicknameActivity", "회원가입 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(this@CreateAccountNicknameActivity, "회원가입 실패: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("CreateAccountNicknameActivity", "네트워크 오류: ${t.message}", t)
                Toast.makeText(this@CreateAccountNicknameActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}