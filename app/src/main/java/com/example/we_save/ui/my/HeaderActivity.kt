package com.example.we_save.ui.my


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.we_save.R
import com.example.we_save.databinding.ActivityHeaderBinding

class HeaderActivity : AppCompatActivity() {

    lateinit var binding: ActivityHeaderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.headerToolbar)

        // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val value = intent.getStringExtra("nextPage").toString()

        // 전달된 키 값으로 프래그먼트 교체
        when (value){
            "MySettingFragment" -> changeFragment(MySettingFragment(), "설정")
            "ProfileEditFragment" -> changeFragment(ProfileEditFragment(), "프로필 수정")
            "MySettingBlockFragment" -> changeFragment(MySettingBlockFragment(), "차단 관리")
            "MySettingAreaFragment" -> changeFragment(MySettingAreaFragment(), "관심 지역 설정")
            "MySettingNoticeFragment" -> changeFragment(MySettingNoticeFragment(), "알림 설정")
            "MySettingUserFragment" -> changeFragment(MySettingUserFragment(), "계정 설정")
            "MySettingUserWithdrawFragment" -> changeFragment(MySettingUserWithdrawFragment(), "계정 설정")
        }


    }

    // 작동 안됨
    override fun onSupportNavigateUp(): Boolean {
        // 뒤로가기 버튼 클릭 시 이전 프래그먼트로 돌아가기
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return true
        }
        return super.onSupportNavigateUp()
    }

    // 프래그먼트 교체 함수
    private fun changeFragment(fragment: Fragment, title: String) {
        binding.headerToolbar.title = title
        // 프래그먼트를 동적으로 로드
        supportFragmentManager.beginTransaction()
            .replace(R.id.header_fragment_container, fragment)
            .addToBackStack(null)
            .commit()

    }

}