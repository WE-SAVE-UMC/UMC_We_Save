package com.example.we_save

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SmsService {
    @POST("api/sms/requests")
    fun requestSms(@Body request: SmsRequest): Call<Void>
    @POST("api/sms/check-sms")
    fun checkSms(@Body checkRequest: CheckSmsRequest): Call<CheckSmsResponse>
}

data class SmsRequest(val phoneNum: String)
data class CheckSmsRequest(val phoneNum: String, val verificationCode: String)
data class CheckSmsResponse(val isSuccess: Boolean, val code: String, val message: String, val result: CheckSmsResult)
data class CheckSmsResult(val message: String, val valid: Boolean)