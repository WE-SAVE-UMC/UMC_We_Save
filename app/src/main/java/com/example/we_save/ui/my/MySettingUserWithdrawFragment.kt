package com.example.we_save.ui.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.ProfileInterface
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMySettingUserWithdrawBinding
import com.example.we_save.ui.createAccount.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySettingUserWithdrawFragment : Fragment() {

    lateinit var binding: FragmentMySettingUserWithdrawBinding
    private val sevenDaysInMillis: Long = 7 * 24 * 60 * 60 * 1000 // 7일을 밀리초로 변환
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySettingUserWithdrawBinding.inflate(inflater, container, false)


        // 탈퇴하기 버튼
        binding.settingUserWithdrawOkBtn.setOnClickListener {
            // 7일 후에 withdraw()를 실행(7일 안에 재로그인 가능)
            Handler(Looper.getMainLooper()).postDelayed({
                withdraw()
            }, sevenDaysInMillis)

            logout()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return binding.root
    }

    // 탈퇴하기
    private fun withdraw(){
        val withdrawService = getRetrofit().create(ProfileInterface::class.java)

        withdrawService.withdraw(getJwt()).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("withdraw/SUCCESS", response.toString())
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("withdraw/FAIL", t.message.toString())
            }

        })
    }


    // 로그아웃
    private fun logout(){
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = spf.edit()
        // 사용자 토큰 삭제
        editor.remove("jwtToken")
        editor.remove("phoneNum")
        editor.apply()
    }

    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }

}