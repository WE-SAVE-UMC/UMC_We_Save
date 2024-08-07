package com.example.we_save.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.databinding.FragmentMySettingBlockBinding

class MySettingBlockFragment : Fragment()  {

    private var blockDatas = ArrayList<Block>()
    lateinit var binding: FragmentMySettingBlockBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMySettingBlockBinding.inflate(inflater, container, false)

        // 뒤로가기 버튼 (차단관리 -> 설정)
//        binding.toolbar.setOnClickListener {
//            intent = Intent(this, MySettingFragment::class.java)
//            startActivity(intent)
//        }

        // 데이터베이스에서 데이터 로드
        val writingDB = WritingDatabase.getInstance(requireContext())!!
        blockDatas.addAll(writingDB.blockDao().getBlocks())

        // 어댑터 설정
        val mySettingBlockRVAdapter = MySettingBlockRVAdapter(blockDatas)
        binding.settingBlockContentRv.adapter = mySettingBlockRVAdapter
        binding.settingBlockContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)



        return binding.root
    }

}