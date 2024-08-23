package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HomeService {
    @POST("/api/home")
    fun getHomeData(@Body request : HomeRequest): Call<HomeResponse>
}

data class HomeRequest(
    val latitude: Double,
    val longitude: Double,
    val regionName: String
)

data class HomeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HomeResult
)

data class HomeResult(
    val postDtos: List<PostDto>,
    val hostPostDtos: List<HostPostDto>,
    val quizId: Int,
    val quizText: String
)

data class PostDto(
    val postId: Int,
    @SerializedName("regionId") val regionId: Int,  // 필드 이름 수정
    val regionName: String,
    val distance: Double,
    val hearts: Int,
    @SerializedName("createAt") val createAt: String,  // 필드 이름 수정
    val title: String,
    @SerializedName("imageUrl") val imageUrl: String?,  // 필드 이름 수정
    @SerializedName("categoryName") val categoryName: String  // 필드 이름 수정
)

data class HostPostDto(
    val postId: Int,
    val title: String,
    val status: String,
    val regionId: Int,
    val regionName: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double,
    val hearts: Int,
    val createAt: String,
    val imageUrl: String?,
    val categoryName: String
)