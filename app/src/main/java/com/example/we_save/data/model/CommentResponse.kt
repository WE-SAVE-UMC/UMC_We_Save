package com.example.we_save.data.model

data class CommentResponse(
    val result: CommentResult? = null,
) : BaseResponse()

data class CommentResult(
    val commentId: Long,
)