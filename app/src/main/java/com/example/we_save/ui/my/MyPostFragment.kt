package com.example.we_save.ui.my

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.we_save.R
import com.example.we_save.data.apiservice.MyPostInterface
import com.example.we_save.data.apiservice.PostRequest
import com.example.we_save.data.apiservice.PostResponse
import com.example.we_save.data.apiservice.PostResult
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMyPostBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostFragment : Fragment() {
//
//    lateinit var binding: FragmentMyPostBinding
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentMyPostBinding.inflate(inflater, container, false)
//
//
//        // BottomSheetBehavior 설정
//        val behavior = BottomSheetBehavior.from(binding.myPostBottomSheet)
//        behavior.state = BottomSheetBehavior.STATE_HIDDEN
//
//        // 서버에서 게시물 가져오기
//        getPosts { posts, error ->
//            if (posts != null) {
//                val postWritingRVAdapter = PostWritingRVAdapter(posts)
//
//                binding.myPostContentRv.adapter = postWritingRVAdapter
//                binding.myPostContentRv.layoutManager = GridLayoutManager(context, 3)
//                Log.d("GETPosts/RESULT", posts.toString())
//
//
//                var completePostsArray = ArrayList<Int>()
//                var select = false // 선택
//                // 선택을 누르면 체크 박스 활성화
//                binding.myPostSelectTv.setOnClickListener {
//                    binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)
//
//                    // 선택 -> 완료
//                    if (!select) {
//                        binding.myPostSelectTv.text = "완료"
//                        binding.myPostSelect.visibility = View.VISIBLE
//
//                        // 바텀 시트 보이게
//                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//                    }
//                    // 완료 -> 선택
//                    else {
//                        completePostsArray.clear()
//                        binding.myPostSelectTv.text = "선택"
//                        binding.myPostSelect.visibility = View.GONE
//
//                        // 바텀 시트를 닫음
//                        behavior.state = BottomSheetBehavior.STATE_HIDDEN
//
//                    }
//                    select = !select
//
//                    // selected의 값을 모두 false로 바꿈
//                    postWritingRVAdapter.clearSelected()
//                    postWritingRVAdapter.updateSelectVisibility()
//                }
//
//
//                var selectAll = false // 전체 선택
//                // 전체 선택
//                binding.myPostSelectAllIv.setOnClickListener {
//                    if (!selectAll) {
//                        binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_on)
//
//                        // selected의 값을 모두 true로 바꿈
//                        postWritingRVAdapter.selectAll()
//                    } else {
//                        binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)
//
//                        // selected의 값을 모두 false로 바꿈
//                        postWritingRVAdapter.clearSelected()
//                    }
//
//                    selectAll = !selectAll
//
//                }
//
//                // 상황 종료 버튼
//                binding.bottomSheetComplete.setOnClickListener {
//                    for(i in completePostsArray.indices){
//                        completePosts(completePostsArray[i])
//                    }
//
//                    // 바텀 시트를 닫음
//                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
//
//                    completePostsArray.clear()
//                    binding.myPostSelectTv.text = "선택"
//                    binding.myPostSelect.visibility = View.GONE
//
//                    select = !select
//
//                    // selected의 값을 모두 false로 바꿈
//                    postWritingRVAdapter.clearSelected()
//                    postWritingRVAdapter.updateSelectVisibility()
//
//                }
//
//
//                postWritingRVAdapter.setMyItemClickListener(object : PostWritingRVAdapter.MyItemClickListener {
//                    // 아이템 선택 시 상세 정보 페이지로 넘어감
//                    override fun onItemClick(post: PostResult) {
//
//                    }
//
//                    // 선택된 아이템을 배열에 추가함
//                    override fun onSelectClick(post: PostResult) {
//
//                        if(post.selected == false){
//                            completePostsArray.add(post.postId)
//                        } else {
//                            completePostsArray.remove(post.postId)
//                        }
//
//                    }
//
//                    // 선택된 아이템의 개수 업데이트
//                    override fun onSelectCountChange(count: Int) {
//                        binding.myPostSelectCountTv.text = postWritingRVAdapter.getSelectedCount().toString()
//                    }
//                })
//
//
//
//
//            }
//        }
//
//        return binding.root
//    }
//
//    // 상황 종료
//    private fun completePosts(postId: Int){
//        val getPostsService = getRetrofit().create(MyPostInterface::class.java)
//
//        getPostsService.completePosts(getJwt(), postId).enqueue(object : Callback<PostResponse>{
//            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
//                Log.d("GETPosts/SUCCESS", response.toString())
//            }
//
//            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
//                Log.d("GETPosts/FAIL", t.message.toString())
//            }
//
//        })
//    }
//
//    private fun getPosts(callback: (ArrayList<PostResult>?, String?) -> Unit) {
//        val getPostsService = getRetrofit().create(MyPostInterface::class.java)
//        val postArray = ArrayList<PostResult>()
//
//        getPostsService.getPosts(getJwt()).enqueue(object : Callback<PostResponse> {
//            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
//                Log.d("GETPosts/SUCCESS", response.toString())
//
//                val resp: PostResponse = response.body()!!
//                for (post in resp.result) {
//                    postArray.add(post)
//                }
//
//                callback(postArray, null)
//            }
//
//            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
//                Log.d("GETPosts/FAIL", t.message.toString())
//                callback(null, t.message)
//            }
//        })
//    }
//
//
//    // 로그인된 사용자의 토큰을 반환하는 함수
//    private fun getJwt(): String{
//        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//
//        return spf.getString("jwtToken", "error").toString()
//    }

    lateinit var binding: FragmentMyPostBinding
    private lateinit var postWritingRVAdapter: PostWritingRVAdapter
    private var completePostsArray = ArrayList<Int>()
    private var select = false // 선택 상태 여부
    private var selectAll = false // 전체 선택 상태 여부
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        // BottomSheetBehavior 설정
        behavior = BottomSheetBehavior.from(binding.myPostBottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        // 서버에서 게시물 가져오기
        loadPosts()

        // 선택 버튼 클릭 리스너 설정
        binding.myPostSelectTv.setOnClickListener {
            handleSelectButtonClick(behavior)
        }

        // 전체 선택 버튼 클릭 리스너 설정
        binding.myPostSelectAllIv.setOnClickListener {
            handleSelectAllButtonClick()
        }

        // 상황 종료 버튼 클릭 리스너 설정
        binding.bottomSheetComplete.setOnClickListener {
            handleCompleteButtonClick(behavior)
        }

        return binding.root
    }

    // 게시물 로드
    private fun loadPosts() {
        getPosts { posts, error ->
            if (posts != null) {
                postWritingRVAdapter = PostWritingRVAdapter(posts)
                binding.myPostContentRv.adapter = postWritingRVAdapter
                binding.myPostContentRv.layoutManager = GridLayoutManager(context, 3)

                Log.d("GETPosts/RESULT", posts.toString())

                postWritingRVAdapter.setMyItemClickListener(object : PostWritingRVAdapter.MyItemClickListener {
                    override fun onItemClick(post: PostResult) {
                        // 아이템 선택 시 상세 정보 페이지로 넘어감
                    }

                    override fun onSelectClick(post: PostResult) {
                        if (post.selected == false) {
                            completePostsArray.add(post.postId)
                        } else {
                            completePostsArray.remove(post.postId)
                        }
                    }

                    override fun onSelectCountChange(count: Int) {
                        binding.myPostSelectCountTv.text = postWritingRVAdapter.getSelectedCount().toString()
                    }
                })
            } else {
                Log.d("GETPosts/FAIL", error ?: "Unknown error")
            }
        }
    }

    // 선택 버튼 클릭 처리
    private fun handleSelectButtonClick(behavior: BottomSheetBehavior<FrameLayout>) {
        binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)

        if (!select) {
            binding.myPostSelectTv.text = "완료"
            binding.myPostSelect.visibility = View.VISIBLE
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            completePostsArray.clear()
            binding.myPostSelectTv.text = "선택"
            binding.myPostSelect.visibility = View.GONE
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        select = !select
        postWritingRVAdapter.clearSelected()
        postWritingRVAdapter.updateSelectVisibility()
    }

    // 전체 선택 버튼 클릭 처리
    private fun handleSelectAllButtonClick() {
        if (!selectAll) {
            binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_on)
            postWritingRVAdapter.selectAll()
        } else {
            binding.myPostSelectAllIv.setImageResource(R.drawable.ic_checkbox_off)
            postWritingRVAdapter.clearSelected()
        }

        selectAll = !selectAll
    }

    // 상황 종료 버튼 클릭 처리
    private fun handleCompleteButtonClick(behavior: BottomSheetBehavior<FrameLayout>) {
        for (postId in completePostsArray) {
            completePosts(postId)
        }

        // 바텀 시트 숨기기
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        completePostsArray.clear()
        binding.myPostSelectTv.text = "선택"
        binding.myPostSelect.visibility = View.GONE
        select = false

        postWritingRVAdapter.clearSelected()
        postWritingRVAdapter.updateSelectVisibility()

        // 게시물 다시 로드
        loadPosts()

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // 프래그먼트를 교체하여 새로고침
        fragmentTransaction.replace(this.id, this).commit()
        
        //postWritingRVAdapter.notifyDataSetChanged()
    }

    // 상황 종료
    private fun completePosts(postId: Int) {
        val getPostsService = getRetrofit().create(MyPostInterface::class.java)

        getPostsService.completePosts(getJwt(), postId).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                Log.d("GETPosts/SUCCESS", response.toString())
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Log.d("GETPosts/FAIL", t.message.toString())
            }
        })
    }

    // 서버에서 게시물 가져오기
    private fun getPosts(callback: (ArrayList<PostResult>?, String?) -> Unit) {
        val getPostsService = getRetrofit().create(MyPostInterface::class.java)
        val postArray = ArrayList<PostResult>()

        getPostsService.getPosts(getJwt()).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                Log.d("GETPosts/SUCCESS", response.toString())

                val resp: PostResponse = response.body()!!
                postArray.addAll(resp.result)
                callback(postArray, null)
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                Log.d("GETPosts/FAIL", t.message.toString())
                callback(null, t.message)
            }
        })
    }

    // 로그인된 사용자의 토큰을 반환하는 함수
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }

}