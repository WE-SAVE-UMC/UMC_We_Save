package com.example.we_save.data.apiservice

import com.example.we_save.ui.alarm.NotificationService
import com.example.we_save.ui.search.HomeSearchService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://114.108.153.82:8080"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun createService(): AdvertisementService {
        return retrofit.create(AdvertisementService::class.java)
    }
    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
    val homeSearchService: HomeSearchService = retrofit.create(HomeSearchService::class.java)

    val notificationService = retrofit.create(NotificationService::class.java)
}