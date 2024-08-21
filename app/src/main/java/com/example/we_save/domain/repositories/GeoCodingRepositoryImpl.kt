package com.example.we_save.domain.repositories

import com.example.we_save.data.model.NaverReverseGeocodingResponse
import com.example.we_save.data.sources.GeocodingRemoteSource
import retrofit2.Response

class GeoCodingRepositoryImpl private constructor() : GeoCodingRepository {
    companion object {
        private var instance: GeoCodingRepository? = null

        fun getInstance(): GeoCodingRepository {
            return instance ?: GeoCodingRepositoryImpl().also {
                instance = it
            }
        }
    }

    private val source by lazy { GeocodingRemoteSource.getInstance() }

    override suspend fun reverseGeocoding(
        latitude: Double,
        longitude: Double
    ): Response<NaverReverseGeocodingResponse> = source.reverseGeocoding("${longitude},${latitude}")
}