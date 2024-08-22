package com.example.we_save

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.databinding.FragmentMainCheckBinding
import com.example.we_save.databinding.FragmentMainDistanceBinding
import com.example.we_save.ui.main.MainRecyclerAdapter
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//확인순
class MainCheckFragment : Fragment() {

    private lateinit var binding: FragmentMainCheckBinding
    private val items = ArrayList<PostDTO>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCheckBinding.inflate(inflater, container, false)
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
        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwtToken", null) ?: return
        val retrofit = Retrofit.Builder()
            .baseUrl("http://114.108.153.82:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(maincheckService::class.java)

        // Request Body 생성
        val requestBody = createRequestBody()

        // API 호출
        val call = api.getSortedData("Bearer $token", requestBody)

        call.enqueue(object : Callback<NearbyPostsResponse> {
            override fun onResponse(
                call: Call<NearbyPostsResponse>,
                response: Response<NearbyPostsResponse>
            ) {
                if (response.isSuccessful) {
                    val nearbyPostsResponse = response.body()
                    nearbyPostsResponse?.result?.let { postList ->
                        items.clear()

                        // hearts 순서대로 정렬 ->>내림차순 사용해서 큰 것부터 확인
                        val sortedPostList = postList.sortedByDescending { it.hearts }

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
        val requestData = RequestData(   // 위도, 경도, 지역 이름을 받는 데이터
            latitude = 25.0,
            longtitude = 50.0,
            regionName = "서울특별시 강남구 역삼동"
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