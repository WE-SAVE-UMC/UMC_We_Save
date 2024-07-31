package com.example.we_save.ui.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.we_save.databinding.FragmentMySettingBinding

class MySettingFragment : Fragment() {

    lateinit var binding: FragmentMySettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySettingBinding.inflate(inflater, container, false)
        val intent = Intent(context, HeaderActivity::class.java)

        // 차단 관리 버튼 -> 차단 관리 액티비티로 이동
        binding.mySettingBlock.setOnClickListener {
            intent.putExtra("nextPage", "MySettingBlockFragment")
            startActivity(intent)
        }

        // 차단 관리 버튼 -> 차단 관리 액티비티로 이동
        binding.mySettingArea.setOnClickListener {
            intent.putExtra("nextPage", "MySettingAreaFragment")
            startActivity(intent)
        }

        // 차단 관리 버튼 -> 차단 관리 액티비티로 이동
        binding.mySettingNotice.setOnClickListener {
            intent.putExtra("nextPage", "MySettingNoticeFragment")
            startActivity(intent)
        }

        // 차단 관리 버튼 -> 차단 관리 액티비티로 이동
        binding.mySettingUser.setOnClickListener {
            intent.putExtra("nextPage", "MySettingUserFragment")
            startActivity(intent)
        }


        return binding.root
    }
}