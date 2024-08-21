package com.example.we_save.common

import java.text.SimpleDateFormat
import java.util.Locale

object Constants {
    const val NAVER_API_BASE_URL = "https://naveropenapi.apigw.ntruss.com/"
    const val KAKAO_API_BASE_URL = "https://dapi.kakao.com/"
    const val BASE_URL = "http://114.108.153.82:8080/"
    const val IMAGE_URL_PREFIX = "http://114.108.153.82:80"

    const val SHARED_PREFERENCE_NAME = "myPrefs"
    const val KEY_TOKEN = "jwtToken"
    const val KEY_USER_ID = "userId"
    const val KEY_TEMPORARY_POST_SAVE = "temporary_save"

    // 0: API 명세서 사건/사고 CRUD 의 12~16 사용, 1: API 명세서 내 근처 사건 사고 검색 6~9 사용
    const val POST_GET_METHOD = 1

    const val DEV_MODE = false

    val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
}