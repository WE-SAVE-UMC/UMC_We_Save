package com.example.we_save.data.apiservice

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BlockInterface{
    @GET("/api/users/blocks")
    fun getBlocks(@Header("Authorization") token: String): Call<BlockResponse>

    // 차단 해제
    @DELETE("/api/users/block/{targetId}")
    fun deleteBlock(
        @Header("Authorization") token: String,
        @Path("targetId") targetId: Int): Call<BlockResponse>

    // 차단하기
    @POST("/api/users/block")
    fun postBlock(
        @Header("Authorization") token: String,
        @Body blockRequest: BlockRequest): Call<BlockResponse>
}