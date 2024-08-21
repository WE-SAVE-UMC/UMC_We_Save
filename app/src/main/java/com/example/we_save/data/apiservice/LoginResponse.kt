package com.example.we_save.data.apiservice

import com.google.android.gms.common.api.Status

data class LoginResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LoginResult
)

data class LoginResult(
    val userId: Int,
    val token: String
)

data class LoginRequest(val phoneNum: String, val password: String)

data class ResetPasswordRequest(val phoneNum: String, val password: String)


