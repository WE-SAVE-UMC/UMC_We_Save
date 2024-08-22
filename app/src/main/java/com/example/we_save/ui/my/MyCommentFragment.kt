package com.example.we_save.ui.my

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.we_save.R
import com.example.we_save.data.apiservice.CommentResponse
import com.example.we_save.data.apiservice.CommentResult
import com.example.we_save.data.apiservice.MyCommentInterface
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.PostResponse
import com.example.we_save.data.apiservice.PostResult
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMyCommentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCommentFragment : Fragment(){
/*
    lateinit var binding: FragmentMyCommentBinding
    var CommentArray = ArrayList<CommentResult>()
    private lateinit var commentRVAdapter: CommentRVAdapter
    private var select = false // 선택 상태 여부
    private var selectAll = false // 전체 선택 상태 여부
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommentBinding.inflate(inflater, container, false)


        // BottomSheetBehavior 설정
        behavior = BottomSheetBehavior.from(binding.myCommentBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        getComments()

//        // 선택 버튼 클릭 리스너 설정
//        binding.myCommentSelectTv.setOnClickListener {
//            handleSelectButtonClick(behavior)
//        }
//
//        // 전체 선택 버튼 클릭 리스너 설정
//        binding.myCommentSelectAllIv.setOnClickListener {
//            handleSelectAllButtonClick()
//        }
//
//
//        // 삭제 버튼 클릭 리스너 설정
//        binding.bottomSheetDelete.setOnClickListener {
//            handleDeleteButtonClick(behavior)
//        }

        // 선택 버튼 클릭 리스너 설정
        binding.myCommentSelectTv.setOnClickListener {
            binding.myCommentSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)

            if (!select) {
                binding.myCommentSelectTv.text = "완료"
                binding.myCommentSelect.visibility = View.VISIBLE
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                CommentArray.clear()
                binding.myCommentSelectTv.text = "선택"
                binding.myCommentSelect.visibility = View.GONE
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            commentRVAdapter.updateSelectVisibility()
            select = !select
        }


        return binding.root
    }

    // 선택 버튼 클릭 처리
    private fun handleSelectButtonClick(behavior: BottomSheetBehavior<FrameLayout>) {
        binding.myCommentSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)

        if (!select) {
            binding.myCommentSelectTv.text = "완료"
            binding.myCommentSelect.visibility = View.VISIBLE
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            CommentArray.clear()
            binding.myCommentSelectTv.text = "선택"
            binding.myCommentSelect.visibility = View.GONE
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        select = !select
        commentRVAdapter.clearSelected()
        commentRVAdapter.updateSelectVisibility()
    }

    // 전체 선택 버튼 클릭 처리
    private fun handleSelectAllButtonClick() {
        if (!selectAll) {
            binding.myCommentSelectAllIv.setImageResource(R.drawable.ic_checkbox_on)
            commentRVAdapter.selectAll()
            CommentArray.clear()

            // 전체 commentId를 CommentArray에 집어넣음
            CommentArray = commentRVAdapter.returnAll()

        } else {
            binding.myCommentSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)
            commentRVAdapter.clearSelected()
            CommentArray.clear()
        }

        selectAll = !selectAll
    }

    // 삭제 버튼 클릭 처리
    private fun handleDeleteButtonClick(behavior: BottomSheetBehavior<FrameLayout>) {
        for (i in CommentArray.indices) {
            deleteComment(CommentArray[i].commentId)
        }

        // 바텀 시트 숨기기
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.myCommentSelectTv.text = "선택"
        binding.myCommentSelect.visibility = View.GONE
        select = false

        // CommentArray를 비움
        CommentArray.clear()


        commentRVAdapter.clearSelected()
        commentRVAdapter.updateSelectVisibility()

        // 게시물 다시 로드
        getComments()

    }

    // 삭제
    private fun deleteComment(commentId: Int) {
        val CommentsService = getRetrofit().create(MyCommentInterface::class.java)

        CommentsService.deleteComment(getJwt(), commentId).enqueue(object : Callback<CommentResponse> {

            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                Log.d("DELETEComment/SUCCESS", response.toString())

                // 게시물 다시 로드
                getComments()
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.d("DELETEComment/FAIL", t.message.toString())
            }
        })
    }

    private fun getComments(){
        val CommentsService = getRetrofit().create(MyCommentInterface::class.java)

        CommentsService.getComments(getJwt()).enqueue(object : Callback<CommentResponse>{
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                val resp: CommentResponse = response.body()!!

                CommentArray.addAll(resp.result)

                commentRVAdapter = CommentRVAdapter(CommentArray)
                binding.myCommentContentRv.adapter = commentRVAdapter
                binding.myCommentContentRv.layoutManager = GridLayoutManager(context, 3)

                commentRVAdapter.setMyItemClickListener(object : CommentRVAdapter.MyItemClickListener{
                    override fun onItemClick(post: CommentResult) {

                        // 아이템 선택 시 상세 정보 페이지로 이동
                        CommentsService.getCommentPost(getJwt(), post.postId).enqueue(object : Callback<PostResponse>{
                            override fun onResponse(
                                call: Call<PostResponse>,
                                response: Response<PostResponse>
                            ) {

                            }

                            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                                Log.d("GETPosts/FAIL", t.message.toString())
                            }

                        })

                    }

                    override fun onSelectClick(post: CommentResult) {
                        if (post.selected == false) {
                            CommentArray.add(post)
                            //post.selected = true
                        } else {
                            CommentArray.remove(post)
                            //post.selected = false
                        }
                    }

                    override fun onSelectCountChange(count: Int) {
                        binding.myCommentSelectCountTv.text = commentRVAdapter.getSelectedCount().toString()
                    }

                })

            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                Log.d("GETPosts/FAIL", t.message.toString())

            }

        })

    }


    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }*/

    lateinit var binding: FragmentMyCommentBinding
    var CommentArray = ArrayList<CommentResult>()
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommentBinding.inflate(inflater, container, false)

        // BottomSheetBehavior 설정
        behavior = BottomSheetBehavior.from(binding.myCommentBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN // 시작 시 BottomSheet 숨기기

        getComments()

        // 선택 클릭 시
        binding.myCommentSelectTv.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED // BottomSheet 보이기
                binding.myCommentSelectTv.text = "완료"
                binding.myCommentSelect.visibility = View.VISIBLE
            } else {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN // BottomSheet 숨기기
                binding.myCommentSelectTv.text = "선택"
                binding.myCommentSelect.visibility = View.GONE
            }
        }


        return binding.root
    }

    private fun getComments(){
        val getCommentsService = getRetrofit().create(MyCommentInterface::class.java)

        getCommentsService.getComments(getJwt()).enqueue(object : Callback<CommentResponse>{
            override fun onResponse(
                call: Call<CommentResponse>,
                response: Response<CommentResponse>
            ) {
                val resp: CommentResponse = response.body()!!

                CommentArray.addAll(resp.result)

                val commentRVAdapter = CommentRVAdapter(CommentArray)
                binding.myCommentContentRv.adapter = commentRVAdapter
                binding.myCommentContentRv.layoutManager = GridLayoutManager(context, 3)


                commentRVAdapter.setMyItemClickListener(object : CommentRVAdapter.MyItemClickListener{
                    override fun onItemClick(post: CommentResult) {
                        // 아이템 선택 시 상세 정보 페이지로 이동
                    }

                })

            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {

            }

        })

    }


    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }
}
