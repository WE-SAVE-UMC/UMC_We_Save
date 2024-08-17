package com.example.we_save.data.apiservice

import com.example.we_save.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @POST("api/auth/users")
    fun createUser(@Body user: User): Call<Void>

    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}