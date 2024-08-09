package com.example.we_save

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface vaildService {
    @GET("/api/auth/check-phone/{number}")
    fun checkPhoneNumber(  // 중복된 전화번호인지 아닌지 판단하는 함수
        @Path("number") phoneNumber: String
    ): Call<PhoneNumberResponse>

    @GET("/api/auth/check-nickname/{nickname}")
    fun checkNickname(    // 중복된 닉네임인지 아닌지 판단하는 함수
        @Path("nickname") nickname: String
    ): Call<NicknameResponse>
}
// 전화번호 유효성
data class PhoneNumberResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PhoneNumberResult
)

data class PhoneNumberResult(
    val isValid: Boolean,
    val message: String
)

//닉네임 유효성
data class NicknameResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: NicknameResult
)

data class NicknameResult(
    val isValid: Boolean
)