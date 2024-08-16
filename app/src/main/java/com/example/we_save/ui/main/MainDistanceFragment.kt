package com.example.we_save.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.NearbyPostsResponse
import com.example.we_save.PostDTO
import com.example.we_save.databinding.FragmentMainDistanceBinding
import com.example.we_save.maincheckService
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MainDistanceFragment : Fragment() {

    private lateinit var binding: FragmentMainDistanceBinding
    private val items = ArrayList<PostDTO>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainDistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        binding.nearAccidentRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = MainRecyclerAdapter(items)
        binding.nearAccidentRecycler.adapter = adapter

        // 데이터 로드
        loadData(adapter)
    }

    private fun loadData(adapter: MainRecyclerAdapter) {
        // Retrofit 설정
        val retrofit = Retrofit.Builder()
            .baseUrl("http://114.108.153.82:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(maincheckService::class.java)

        // Request Body 생성
        val requestBody = createRequestBody()

        // API 호출
        val call = api.getSortedData(requestBody)

        call.enqueue(object : Callback<NearbyPostsResponse> {
            override fun onResponse(
                call: Call<NearbyPostsResponse>,
                response: Response<NearbyPostsResponse>
            ) {
                if (response.isSuccessful) {
                    val nearbyPostsResponse = response.body()
                    nearbyPostsResponse?.result?.let { postList ->
                        items.clear()

                        // 거리순으로 정렬하는 부분
                        val sortedPostList = postList.sortedBy { it.distance }

                        items.addAll(sortedPostList)
                        adapter.notifyDataSetChanged()
                        Log.d("API Success", "Data loaded successfully: ${sortedPostList.size} items loaded.")
                        for (post in sortedPostList) {
                            Log.d("API Success", "Post: $post")
                        }
                    }
                } else {
                    Log.e(
                        "API Error",
                        "Response Code: ${response.code()}, Message: ${response.message()}"
                    )
                    Log.e("API Error Body", "Error Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<NearbyPostsResponse>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}")
            }
        })
    }

    private fun createRequestBody(): RequestBody {
        val requestData = RequestData(   //위도,경도,지역 이름을 받는 데이터(나중에 수정 필요!!)
            latitude = 25.0,
            longtitude = 50.0,
            regionName = "서울특별시 노원구 월계동"
        )

        val gson = Gson()
        val json = gson.toJson(requestData)
        return RequestBody.create("application/json".toMediaTypeOrNull(), json)
    }

    data class RequestData(
        val latitude: Double,
        val longtitude: Double,
        val regionName: String
    )
}
