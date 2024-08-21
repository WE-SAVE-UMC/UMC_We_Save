package com.example.we_save.ui.my


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.we_save.R
import com.example.we_save.databinding.ActivityHeaderBinding
import com.example.we_save.ui.MainActivity

class HeaderActivity : AppCompatActivity() {

    lateinit var binding: ActivityHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeaderBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.headerToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Toolbar 설정
        setSupportActionBar(binding.headerToolbar)

        // 뒤로가기 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // nextPage가 null일 경우 기본값으로 MySettingFragment 설정
        val nextPage = intent?.getStringExtra("nextPage") ?: "MySettingFragment"


        // 전달된 키 값으로 프래그먼트 교체
        when (nextPage){
            "MySettingFragment" -> changeFragment(MySettingFragment(), "설정")
            "ProfileEditFragment" -> changeFragment(ProfileEditFragment(), "프로필 수정")
            "MySettingBlockFragment" -> changeFragment(MySettingBlockFragment(), "차단 관리")
            "MySettingAreaFragment" -> changeFragment(MySettingAreaFragment(), "관심 지역 설정")
            "MySettingNoticeFragment" -> changeFragment(MySettingNoticeFragment(), "알림 설정")
            "MySettingUserFragment" -> changeFragment(MySettingUserFragment(), "계정 설정")
            "MySettingUserWithdrawFragment" -> changeFragment(MySettingUserWithdrawFragment(), "계정 설정")

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val currentPage = intent.getStringExtra("nextPage").toString()
        val intentMain = Intent(this, MainActivity::class.java)
        val intentHeader = Intent(this, HeaderActivity::class.java)

        return when (item.itemId) {
            android.R.id.home -> {
                when(currentPage){
                    "MySettingBlockFragment", "MySettingAreaFragment", "MySettingNoticeFragment", "MySettingUserFragment" -> {
                        intent.putExtra("nextPage","MySettingFragment")
                        startActivity(intentHeader)
                    }
                    "ProfileEditFragment", "MySettingFragment" -> {
                        intentMain.putExtra("selectedPage", 3)
                        startActivity(intentMain)
                    }
                    "MySettingUserWithdrawFragment"-> {
                        intent.putExtra("nextPage","MySettingUserFragment")
                        startActivity(intentHeader)
                    }
                    else -> {
                    }

                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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