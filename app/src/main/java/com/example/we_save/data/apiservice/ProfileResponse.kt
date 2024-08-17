package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName


data class ProfileResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: String,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: ProfileResult
)

data class ProfileResult(
    @SerializedName("userId")val userId: Int,
    @SerializedName("phoneNum")val phoneNum: String,
    @SerializedName("nickname")val nickname: String,
    @SerializedName("imageUrl")val imageUrl: String,
    @SerializedName("status")val status: String
)

data class ProfileRequest(
    //@SerializedName("profileImage")val profileImage: String
    @SerializedName("nickname")val nickname: String,
)