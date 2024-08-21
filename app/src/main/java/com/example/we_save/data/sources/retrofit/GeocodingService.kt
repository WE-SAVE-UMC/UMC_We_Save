package com.example.we_save.data.sources.retrofit

import com.example.we_save.data.model.KakaoReverseGeocodingResponse
import com.example.we_save.data.model.NaverReverseGeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GeocodingService {
    /**
     * Naver reverse geocoding
     *
     * @param coords longitude,latitude (ex) "127.3042267,37.4039267")
     */
    @Headers(
        "X-NCP-APIGW-API-KEY-ID: 4jqulkagxc",
        "X-NCP-APIGW-API-KEY: og4FDsiodGimHbz5ASFJpQxeO1yFQ9TiwUNMXrld"
    )
    @GET("/map-reversegeocode/v2/gc?output=json&orders=addr")
    suspend fun reverseGeocoding(@Query("coords") coords: String): Response<NaverReverseGeocodingResponse>

    /**
     * Kakao reverse geocoding
     */
    @Headers(
        "Authorization: KakaoAK 952a9d4163dd2be128a63e8a6599b6a8"
    )
    @GET("/v2/local/geo/coord2address.JSON")
    suspend fun reverseGeocoding(
        @Query("y") latitude: Double,
        @Query("x") longitude: Double
    ): Response<KakaoReverseGeocodingResponse>
}
