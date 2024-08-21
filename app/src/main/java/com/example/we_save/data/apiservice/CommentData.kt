package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("isSuccess")val isSuccess: Boolean,
    @SerializedName("code")val code: String,
    @SerializedName("message")val message: String,
    @SerializedName("result")val result: ArrayList<CommentResult>
)

data class CommentResult(
    @SerializedName("commentId")val commentId: Int,
    @SerializedName("postId")val postId: Int, // 댓글 단 글의 id
    @SerializedName("title")val title: String, // 댓글 단 글의 title
    @SerializedName("status")val status: String, // 종료 여부
    @SerializedName("category")val category: String,
    @SerializedName("content")val content: String,
    @SerializedName("regionName")val regionName: String,
    @SerializedName("imageUrl")val imageUrl: String,
    var selected: Boolean = false,      // true면 선택됨
    var selectedVisible: Boolean = false
)
