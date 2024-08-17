package com.example.we_save.ui.my

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsAnimation.Callback
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.R
import com.example.we_save.SplashActivity
import com.example.we_save.data.apiservice.LoginResponse
import com.example.we_save.data.apiservice.ProfileInterface
import com.example.we_save.data.apiservice.ProfileRequest
import com.example.we_save.data.apiservice.ProfileResponse
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMyPostBinding
import com.example.we_save.databinding.FragmentProfileEditBinding
import com.example.we_save.ui.main.pages.MyFragment
import retrofit2.Call
import retrofit2.Response

class ProfileEditFragment : Fragment() {

    lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        getProfile()

        binding.profileEditSaveBtn.setOnClickListener {
            //setProfile(/*binding.profileEditPhotoIv.toString(),*/ binding.profileEditNameEt.text.toString())
            getProfile()
        }

        return binding.root
    }

    // 로그인된 사용자의 토큰을 반환
    private fun getJwt(): String{
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        return spf.getString("jwtToken", "error").toString()
    }

    // 기본 프로필 정보
    private fun getProfile(){
        val profileService = getRetrofit().create(ProfileInterface::class.java)
        profileService.getProfile(getJwt()).enqueue(object: retrofit2.Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                Log.d("GETProfile/SUCCESS", response.toString())

                val resp: ProfileResponse = response.body()!!
                when(resp.code){
                    // 로딩에 성공한 경우
                    "COMMON200" -> {
                        // 프로필 사진 설정
                        val img = "http://114.108.153.82:80${resp.result.imageUrl}"

                        Glide.with(requireContext())
                            .load(img)
                            .apply(RequestOptions().placeholder(R.drawable.ic_profile)) // 로딩 중에 보여줄 이미지
                            .error(R.drawable.ic_profile) // 오류 발생 시 보여줄 이미지
                            .circleCrop()
                            .into(binding.profileEditPhotoIv)

                        // 닉네임 설정
                        binding.profileEditNameEt.setHint(resp.result.nickname)

                        // 전화번호 설정
                        val phoneNum = resp.result.phoneNum
                        val formattedPhoneNum = phoneNum.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
                        binding.profileEditPhoneTv.text = formattedPhoneNum

                    }
                    // 로딩에 실패한 경우
                    "COMMON401" -> binding.profileEditNameEt.setHint("네트워크 오류")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d("GETProfile/FAIL",t.message.toString())
            }

        })

        Log.d("GETProfile", "FINISH")

    }

    // 프로필 수정
    private fun setProfile(/*profileImage: String,*/ nickName: String){
        val profileService = getRetrofit().create(ProfileInterface::class.java)
        profileService.setProfile(getJwt(), ProfileRequest(/*profileImage*/nickName)).enqueue(object: retrofit2.Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                Log.d("PUTProfile/SUCCESS",response.toString())

                val resp: ProfileResponse = response.body()!!
                when(resp.code){
                    // 로딩에 성공한 경우
                    "COMMON200" -> Log.d("성공ㅎㅎㅎ",response.toString())
                    // 로딩에 실패한 경우
                    "COMMON401" -> Log.d("실퍃ㅎㅎ",response.toString())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d("PUTProfile/FAIL",t.message.toString())
            }

        })

        Log.d("PUTProfile", "FINISH")

    }






}
