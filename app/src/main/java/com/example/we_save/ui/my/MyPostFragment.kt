package com.example.we_save.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.databinding.FragmentMyPostBinding

class MyPostFragment : Fragment() {

    lateinit var binding: FragmentMyPostBinding

    private var writingDatas = ArrayList<Writing>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)


        // 데이터베이스에서 데이터 로드
        val writingDB = WritingDatabase.getInstance(requireContext())!!
        writingDatas.addAll(writingDB.writingDao().getWritings())

        // 어댑터 설정
        val postWritingRVAdapter = PostWritingRVAdapter(writingDatas)
        binding.mypagePostContentRv.adapter = postWritingRVAdapter
        binding.mypagePostContentRv.layoutManager = GridLayoutManager(context, 3)


        return binding.root
    }
}