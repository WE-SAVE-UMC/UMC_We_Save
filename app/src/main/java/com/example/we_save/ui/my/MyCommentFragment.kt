package com.example.we_save.ui.my

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.we_save.data.apiservice.CommentResponse
import com.example.we_save.data.apiservice.CommentResult
import com.example.we_save.data.apiservice.MyCommentInterface
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.PostResponse
import com.example.we_save.data.apiservice.PostResult
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMyCommentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCommentFragment : Fragment(){

    lateinit var binding: FragmentMyCommentBinding
    var CommentArray = ArrayList<CommentResult>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommentBinding.inflate(inflater, container, false)


        getComments()


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
