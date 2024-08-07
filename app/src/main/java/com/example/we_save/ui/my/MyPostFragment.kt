package com.example.we_save.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.R
import com.example.we_save.databinding.FragmentMyPostBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

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
        binding.myPostContentRv.adapter = postWritingRVAdapter
        binding.myPostContentRv.layoutManager = GridLayoutManager(context, 3)


        var select = false // 선택

        // BottomSheetBehavior 설정
        val behavior = BottomSheetBehavior.from(binding.myPostBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        // 선택을 누르면 체크 박스 활성화
        binding.myPostSelectTv.setOnClickListener {
            binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)

            // 선택 -> 완료
            if(!select){
                binding.myPostSelectTv.text = "완료"
                binding.myPostSelect.visibility = View.VISIBLE

                behavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
            // 완료 -> 선택
            else{
                binding.myPostSelectTv.text = "선택"
                binding.myPostSelect.visibility = View.GONE

                // 바텀 시트를 닫음
                behavior.state = BottomSheetBehavior.STATE_HIDDEN

            }
            select = !select

            // selected의 값을 모두 false로 바꿈
            postWritingRVAdapter.clearSelected()
            postWritingRVAdapter.updateSelectVisibility()
        }


        var selectAll = false // 전체 선택
        // 전체 선택
        binding.myPostSelectAllIv.setOnClickListener {
            if(!selectAll) {
                binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_on)

                // selected의 값을 모두 true로 바꿈
                postWritingRVAdapter.selectAll()
            }
            else{
                binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)

                // selected의 값을 모두 false로 바꿈
                postWritingRVAdapter.clearSelected()
            }

            selectAll = !selectAll

        }

        binding.myPostSelectCountTv.text = postWritingRVAdapter.getSelectedCount().toString()




        postWritingRVAdapter.setMyItemClickListener(object: PostWritingRVAdapter.MyItemClickListener{
            override fun onItemClick(writing: Writing) {
                // 항목 클릭 시 처리 로직

            }

            override fun onSelectClick(writing: Writing) {
                // 선택 클릭 시 처리 로직
            }

            override fun onSelectCountChange(count: Int) {
                binding.myPostSelectCountTv.text = postWritingRVAdapter.getSelectedCount().toString()
            }
        })


        return binding.root
    }

}