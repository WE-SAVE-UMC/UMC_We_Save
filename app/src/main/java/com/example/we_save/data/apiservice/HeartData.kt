package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName

data class HeartRequest(
    @SerializedName("regionName") val regionName: String
)
data class HeartResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: HeartResultContainer
)

data class HeartResultContainer(
    @SerializedName("heartDTOs") val heartDTOs: ArrayList<HeartResult>
)

data class HeartResult(
    @SerializedName("region_name") val regionName: String, // 지역 풀 네임
    @SerializedName("hear_id") val regionId: Int // 관심 지역 ID
)
