package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName

data class BlockRequest(
    val targetId: Int
)
data class BlockResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result
)

data class Result(
    @SerializedName("blockUserList") val blockUserList: ArrayList<BlockResult>
)

data class BlockResult(
    @SerializedName("userId") val userId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImage") val profileImage: ProfileImage
)

data class ProfileImage(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("filePath") val filePath: String
)
