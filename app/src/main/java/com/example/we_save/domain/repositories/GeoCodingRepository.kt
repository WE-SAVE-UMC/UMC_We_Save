package com.example.we_save.domain.repositories

import com.example.we_save.data.model.NaverReverseGeocodingResponse
import retrofit2.Response

interface GeoCodingRepository {
    suspend fun reverseGeocoding(
        latitude: Double,
        longitude: Double
    ): Response<NaverReverseGeocodingResponse>
}