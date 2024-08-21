package com.example.we_save.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.we_save.R
import com.example.we_save.databinding.ActivityMainBinding
import com.example.we_save.ui.main.MainFragment
import com.example.we_save.ui.my.Block
import com.example.we_save.ui.my.Writing
import com.example.we_save.ui.my.WritingDatabase
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Writing 더미 데이터 생성
        inputDummyWritings()
        Log.d("확인", "확인됨")

        // Block 더미 데이터 생성
        inputDummyBlocks()

        supportFragmentManager.commit {
            replace(R.id.fragment_container, MainFragment(), "root")
        }

        with(binding) {
            val badgeDrawable = BadgeDrawable.create(this@MainActivity).apply {
                backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.red_10)
            }

            // Badge 추가하는 방법 (삭제는 BadgeUtils.detachBadgeDrawable() 호출)
            BadgeUtils.attachBadgeDrawable(badgeDrawable, toolbar1, R.id.action_notification)
        }

        lifecycleScope.launch {
            // Location preload
            viewModel.address.collectLatest {}
        }
    }

    // 작성된 글 더미 데이터
    private fun inputDummyWritings() {
        val writingDB = WritingDatabase.getInstance(this)!!
        val writings = writingDB.writingDao().getWritings()

        if (writings.isNotEmpty()) return

        writingDB.writingDao().insert(
            Writing(
                "성신여대 화재",
                "삼선동2가 389",
                R.drawable.img_fire,
                false
            )
        )

        writingDB.writingDao().insert(
            Writing(
                "제목 예시2",
                "삼선동2가 389",
                R.drawable.img_fire,
                false
            )
        )

        writingDB.writingDao().insert(
            Writing(
                "제목 예시3",
                "삼선동2가 389",
                R.drawable.img_fire,
                true
            )
        )

        writingDB.writingDao().insert(
            Writing(
                "제목 예시4",
                "삼선동2가 389",
                R.drawable.img_fire,
                true
            )
        )

        val _writings = writingDB.writingDao().getWritings()
        Log.d("writing Data", _writings.toString())

    }

    // 차단 더미 데이터
    private fun inputDummyBlocks() {
        val writingDB = WritingDatabase.getInstance(this)!!
        val blocks = writingDB.blockDao().getBlocks()

        if (blocks.isNotEmpty()) return

        writingDB.blockDao().insert(
            Block(
                R.drawable.ic_profile,
                "닉네임 예시",
                true
            )
        )

        writingDB.blockDao().insert(
            Block(
                R.drawable.ic_profile,
                "닉네임 예시2",
                false
            )
        )

        val _blocks = writingDB.blockDao().getBlocks()
        Log.d("block Data", _blocks.toString())

    }
}