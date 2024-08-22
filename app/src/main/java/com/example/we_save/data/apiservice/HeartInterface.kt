package com.example.we_save.data.apiservice

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HeartInterface {

    @GET("/api/heart")
    fun getHeart(@Header("Authorization") token: String): Call<HeartResponse>

    // 관심 지역 등록
    @POST("/api/heart")
    fun postHeart(
        @Header("Authorization") token: String,
        @Body heartRequest: HeartRequest): Call<HeartPostResponse>

    // 관심 지역 삭제
    @DELETE("/api/heart/{region-id}")
    fun deleteHeart(
        @Header("Authorization") token: String,
        @Path("region-id") regionId: Int): Call<HeartResponse>


}