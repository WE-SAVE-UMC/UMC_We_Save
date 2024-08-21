package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName

data class PostRequest(
    @SerializedName("postId")val postId: Int
)
data class PostResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: String,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: ArrayList<PostResult>
)

data class PostResult(
    @SerializedName("postId")val postId: Int,
    @SerializedName("title")val title: String,
    @SerializedName("status")val status: String,
    @SerializedName("regionName")val regionName: String,
    @SerializedName("createAt")val createAt: String,
    @SerializedName("imageUrl")val imageUrl: String,
    var selected: Boolean = false,      // true면 선택됨
    var selectedVisible: Boolean = false
)


