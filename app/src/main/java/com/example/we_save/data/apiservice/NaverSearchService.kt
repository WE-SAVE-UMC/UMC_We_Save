package com.example.we_save.data.apiservice

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverSearchService {
    @GET("v1/search/local.json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("display") display: Int,
        @Query("start") start: Int,
        @Query("sort") sort: String,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String
    ): PlaceSearchResponse
}
data class PlaceSearchResponse(
    val items: List<Place>
)

data class Place(
    val title: String,
    val mapx: String,
    val mapy: String
)