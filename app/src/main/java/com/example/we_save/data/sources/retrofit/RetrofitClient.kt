package com.example.we_save.data.sources.retrofit

import com.example.we_save.common.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)  // baseUrl 형식 확인
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
