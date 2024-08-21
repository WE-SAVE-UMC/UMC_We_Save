package com.example.we_save.data.model

data class PostResponse(
    val result: PostResult? = null,
) : BaseResponse()

data class PostResult(
    val postId: Long,
)