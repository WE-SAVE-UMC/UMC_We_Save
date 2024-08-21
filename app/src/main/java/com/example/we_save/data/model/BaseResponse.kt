package com.example.we_save.data.model

open class BaseResponse(
    val isSuccess: Boolean = false,
    val code: String = "",
    val message: String = "",
)