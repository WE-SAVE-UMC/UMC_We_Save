package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName

data class HeartRequest(
    @SerializedName("regionName") val regionName: String
)
data class HeartResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<HeartResult>
)



data class HeartResult(
    @SerializedName("regionName") val regionName: String, // 지역 풀 네임
    @SerializedName("regionId") val regionId: Int // 관심 지역 ID
)

data class HeartPostResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: HeartPostResult
)
data class HeartPostResult(
    @SerializedName("regionId") val regionId: Int
)
