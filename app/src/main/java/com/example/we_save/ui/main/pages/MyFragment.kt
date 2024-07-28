package com.example.we_save.ui.main.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.R
import com.example.we_save.databinding.FragmentMyBinding
import com.example.we_save.ui.my.MyCommentFragment
import com.example.we_save.ui.my.MyPostFragment
import com.example.we_save.ui.my.MySettingActivity
import com.example.we_save.ui.my.PostWritingRVAdapter
import com.example.we_save.ui.my.ProfileEditActivity
import com.google.android.material.tabs.TabLayout

class MyFragment : Fragment() {

    lateinit var binding: FragmentMyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyBinding.inflate(inflater, container, false)


        // 설정 버튼 전환
        binding.mypageSettingIv.setOnClickListener {
            startActivity(Intent(activity, MySettingActivity::class.java))
        }

        // 프로필 수정 버튼 전환
        binding.mypageProfileEdltBtn.setOnClickListener {
            startActivity(Intent(activity, ProfileEditActivity::class.java))
        }

        // 아무 탭도 선택되지 않았을 때 첫 화면
        childFragmentManager.beginTransaction()
            .replace(R.id.mypage_container_fl, MyPostFragment())
            .commit()

        // Tab 설정
        binding.mypageContentTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            // tab이 선택되었을 때
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        // 작성된 글 프래그먼트로 이동
                        childFragmentManager.beginTransaction()
                            .replace(R.id.mypage_container_fl, MyPostFragment())
                            .commit()
                    }
                    1 -> {
                        // 댓글 단 글 프래그먼트로 이동
                        childFragmentManager.beginTransaction()
                            .replace(R.id.mypage_container_fl, MyCommentFragment())
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
}