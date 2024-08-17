package com.example.we_save.data.apiservice

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface ProfileInterface {
    @GET("/api/auth/users")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @PUT("/api/auth/users")
    fun setProfile(
        @Header("Authorization") token: String,
        @Body profileRequest: ProfileRequest): Call<ProfileResponse>
}