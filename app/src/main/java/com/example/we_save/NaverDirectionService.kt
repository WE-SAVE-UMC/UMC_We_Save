package com.example.we_save

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverDirectionService {
    @GET("map-direction/v1/driving")
    suspend fun getDrivingDirections(
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String
    ): DirectionResponse

    @GET("map-direction/v1/walking")
    suspend fun getWalkingDirections(
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String
    ): DirectionResponse

    @GET("map-direction/v1/bicycling")
    suspend fun getBicyclingDirections(
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String
    ): DirectionResponse
}