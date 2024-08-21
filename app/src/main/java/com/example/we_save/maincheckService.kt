package com.example.we_save

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface maincheckService {
    @POST("/api/home/sort/distance")  // 거리순
    fun getSortedData(
        @Body body: RequestBody
    ): Call<NearbyPostsResponse>

    @POST("/api/home/sort/recent")  // 최신순
    fun getRecentData(
        @Body body: RequestBody
    ): Call<NearbyPostsResponse>

    @POST("/api/home/sort/top")
    fun getTopData(
        @Body body: RequestBody
    ): Call<NearbyPostsResponse>
}

// 데이터 클래스 정의 (API 응답 처리)
data class NearbyPostsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<PostDTO>
)



data class PostDTO(
    val postId: Int,
    val region_id: Int,
    val distance: Double,
    val hearts: Int,
    val regionName: String,
    val create_at: String,
    val imageUrl: String,
    val categoryName: String
)