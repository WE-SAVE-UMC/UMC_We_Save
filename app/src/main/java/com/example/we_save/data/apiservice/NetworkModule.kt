package com.example.we_save.data.apiservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://114.108.153.82:8080/"

fun getRetrofit(): Retrofit {
    val retrofit= Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit
}