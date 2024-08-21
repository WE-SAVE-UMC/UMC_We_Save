package com.example.we_save.data.sources

import com.example.we_save.common.Constants
import com.example.we_save.data.model.NaverReverseGeocodingResponse
import com.example.we_save.data.sources.retrofit.GeocodingService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeocodingRemoteSource private constructor() {
    companion object {
        private var instance: GeocodingRemoteSource? = null

        fun getInstance(): GeocodingRemoteSource {
            return instance ?: GeocodingRemoteSource().also {
                instance = it
            }
        }
    }

    private val naverService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.NAVER_API_BASE_URL)  // baseUrl 형식 확인
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

    private val kakaoService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.KAKAO_API_BASE_URL)  // baseUrl 형식 확인
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

    suspend fun reverseGeocoding(coords: String): Response<NaverReverseGeocodingResponse> {
        val result = naverService.reverseGeocoding(coords)
        result.body()?.longitude = coords.split(",")[0].toDoubleOrNull() ?: 0.0
        result.body()?.latitude = coords.split(",")[1].toDoubleOrNull() ?: 0.0
        return result
    }

    suspend fun reverseGeocoding(latitude: Double, longitude: Double) =
        kakaoService.reverseGeocoding(latitude, longitude)
}