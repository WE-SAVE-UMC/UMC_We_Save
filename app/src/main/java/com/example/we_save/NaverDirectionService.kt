package com.example.we_save

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverDirectionService {
    @GET("driving")
    suspend fun getDrivingDirections(
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String
    ): DrivingResponse
}
data class DrivingResponse(
    val route: Route?
)

data class Route(
    val traoptimal: List<Traoptimal>?
)

data class Traoptimal(
    val path: List<List<Double>>?,
    val summary: Summary?
)

data class Summary(
    val duration: Int?
)