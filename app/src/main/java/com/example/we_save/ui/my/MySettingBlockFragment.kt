package com.example.we_save.ui.my

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.data.apiservice.BlockInterface
import com.example.we_save.data.apiservice.BlockRequest
import com.example.we_save.data.apiservice.BlockResponse
import com.example.we_save.data.apiservice.BlockResult
import com.example.we_save.data.apiservice.ProfileInterface
import com.example.we_save.data.apiservice.ProfileResponse
import com.example.we_save.data.apiservice.getRetrofit
import com.example.we_save.databinding.FragmentMySettingBlockBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        getBlocks()

        return binding.root
    }

    private fun getBlocks(){
        val BlockService = getRetrofit().create(BlockInterface::class.java)
        val BlockArray = ArrayList<BlockResult>()

        BlockService.getBlocks(getJwt()).enqueue(object : Callback<BlockResponse>{
            override fun onResponse(call: Call<BlockResponse>, response: Response<BlockResponse>) {
                val resp: BlockResponse = response.body()!!
                Log.d("GETBlocks/SUCCESS", response.toString())

                BlockArray.addAll(resp.result.blockUserList)

                val mySettingBlockRVAdapter = MySettingBlockRVAdapter(BlockArray)
                binding.settingBlockContentRv.adapter = mySettingBlockRVAdapter
                binding.settingBlockContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                mySettingBlockRVAdapter.setMyItemClickListener(object : MySettingBlockRVAdapter.MyItemClickListener{
                    override fun onItemClick(block: BlockResult) {
                        // 아이템 전체 선택 시
                    }

                    override fun onBlockReleaseBtnClick(userId: Int) {
                        // 차단해제
                        deleteBlock(userId)
                    }

                    override fun onBlockBtnClick(userId: Int) {
                        // 차단하기
                        postBlock(userId)
                    }

                })

            }

            override fun onFailure(call: Call<BlockResponse>, t: Throwable) {
                Log.d("GETBlocks/FAIL", t.message.toString())
            }

        })
    }

    // 차단 해제
    private fun deleteBlock(targetId: Int){
        val BlockService = getRetrofit().create(BlockInterface::class.java)

        BlockService.deleteBlock(getJwt(), targetId).enqueue(object : Callback<BlockResponse>{
            override fun onResponse(call: Call<BlockResponse>, response: Response<BlockResponse>) {
                Log.d("DELETEBlocks/SUCCESS", response.toString())
            }

            override fun onFailure(call: Call<BlockResponse>, t: Throwable) {
                Log.d("DELETEBlocks/FAIL", t.message.toString())
            }

        })
    }

    // 차단하기
    private fun postBlock(targetId: Int){
        val BlockService = getRetrofit().create(BlockInterface::class.java)
        val blockRequest = BlockRequest(targetId)


        BlockService.postBlock(getJwt(), blockRequest).enqueue(object : Callback<BlockResponse>{
            override fun onResponse(call: Call<BlockResponse>, response: Response<BlockResponse>) {
                Log.d("POSTBlocks/SUCCESS", response.toString())
            }

            override fun onFailure(call: Call<BlockResponse>, t: Throwable) {
                Log.d("POSTBlocks/FAIL", t.message.toString())
            }

        })
    }

    // 로그인된 사용자의 토큰을 반환
    private fun getJwt(): String {
        val spf = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return spf.getString("jwtToken", "error").toString()
    }

}