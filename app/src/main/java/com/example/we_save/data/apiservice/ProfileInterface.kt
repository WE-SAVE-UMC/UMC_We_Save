package com.example.we_save.data.apiservice

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileInterface {
    @GET("/api/auth/users")
    fun getProfile(@Header("Authorization") token: String): Call<ProfileResponse>

    @Multipart
    @PUT("/api/auth/users")
    fun setProfile(
        @Header("Authorization") token: String,
        @Part profileImage: MultipartBody.Part?,
        @Part("nickname") nickname: RequestBody
    ): Call<ProfileResponse>

    // 탈퇴하기
    @DELETE("/api/auth/users")
    fun withdraw(@Header("Authorization") token: String): Call<LoginResponse>
}