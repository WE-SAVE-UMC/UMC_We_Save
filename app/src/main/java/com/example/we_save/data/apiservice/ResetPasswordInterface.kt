package com.example.we_save.data.apiservice

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ResetPasswordInterface {
    @POST("/api/auth/login")
    fun checkPassword(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @PATCH("/api/auth/users")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<LoginResponse>
}