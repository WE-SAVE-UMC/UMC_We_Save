package com.example.we_save.ui.main.pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.we_save.R
import com.example.we_save.data.apiservice.ProfileInterface
import com.example.we_save.data.apiservice.ProfileResponse
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMyBinding
import com.example.we_save.ui.my.HeaderActivity
import com.example.we_save.ui.my.MyCommentFragment
import com.example.we_save.ui.my.MyPostFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Response

class MyFragment : Fragment() {

    lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false)

        getProfile()


        val intent = Intent(context, HeaderActivity::class.java)

        // 설정 버튼 전환
        binding.mySettingIv.setOnClickListener {

            intent.putExtra("nextPage", "MySettingFragment")
            startActivity(intent)
        }

        // 프로필 수정 버튼 전환
        binding.myProfileEditBtn.setOnClickListener {
            intent.putExtra("nextPage", "ProfileEditFragment")
            startActivity(intent)
        }

        // Tab에서 아무 탭도 선택되지 않았을 때 첫 화면
        childFragmentManager.beginTransaction()
            .replace(R.id.my_container_fl, MyPostFragment())
            .commit()

        // Tab 설정
        binding.myContentTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            // tab이 선택되었을 때
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        // 작성된 글 프래그먼트로 이동
                        childFragmentManager.beginTransaction()
                            .replace(R.id.my_container_fl, MyPostFragment())
                            .commit()
                    }
                    1 -> {
                        // 댓글 단 글 프래그먼트로 이동
                        childFragmentManager.beginTransaction()
                            .replace(R.id.my_container_fl, MyCommentFragment())
                            .commit()
                    }
                }
            }

            // tab이 선택되지 않았을 때
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        return binding.root
    }

    // 기본 프로필 정보
    private fun getProfile(){
        val profileService = getRetrofit().create(ProfileInterface::class.java)
        profileService.getProfile(getJwt()).enqueue(object: retrofit2.Callback<ProfileResponse>{
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                Log.d("GETProfile/SUCCESS",response.toString())

                // response.body()가 null인지 확인
                val resp: ProfileResponse? = response.body()
                if (resp != null) {
                    when (resp.code) {
                        // 로딩에 성공한 경우
                        "COMMON200" -> {
                            val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                            val editor = spf.edit()
                            // myPrefs에 전화번호 저장
                            editor.putString("phoneNum", resp.result.phoneNum)
                            editor.apply()

                            // 프로필 사진 설정
                            val img = "http://114.108.153.82:80${resp.result.imageUrl}"

                            Glide.with(requireContext())
                                .load(img)
                                .apply(RequestOptions().placeholder(R.drawable.ic_profile)) // 로딩 중에 보여줄 이미지
                                .error(R.drawable.ic_profile) // 오류 발생 시 보여줄 이미지
                                .circleCrop()
                                .into(binding.myProfileIv)

                            // 닉네임 설정
                            binding.myProfileNameTv.text = resp.result.nickname
                        }
                        // 로딩에 실패한 경우
                        "COMMON401" -> binding.myProfileNameTv.setHint("네트워크 오류")
                    }
                } else {
                    Log.e("GETProfile/ERROR", "Response body is null")
                    binding.myProfileNameTv.setHint("로딩 실패")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Log.d("GETProfile/FAIL",t.message.toString())
            }

        })

        Log.d("GETProfile", "FINISH")

    }

    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String{
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        return spf.getString("jwtToken", "error").toString()
    }


}