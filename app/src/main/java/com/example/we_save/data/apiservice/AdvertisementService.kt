package com.example.we_save.data.apiservice

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdvertisementService {
    @GET("/api/ads/quiz")
    fun getQuiz(): Call<GetQuizResponse>

    @POST("/api/ads/answer")
    fun submitQuizResponse(@Body request: QuizResponseRequest): Call<QuizResponse>
}
////get
data class GetQuizResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: QuizResult
)

data class QuizResult(
    val adId: Int,
    val question: String,
    val options: List<Option>
)

data class Option(
    val optionId: Int,
    val text: String,
    val isCorrect: Boolean,
    val responseText: String,
    val imageUrl: String,
    val redirectUrl: String
)

////////////////////////  post
data class QuizResponseRequest(
    val adId: Int,
    val selectedOptionId: Int
)

data class QuizResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: QuizResult1
)

data class QuizResult1(
    val correctMessage: String?,
    val incorrectMessage: String?,
    val imageUrl: String,
    val correct: Boolean
)