package com.example.we_save.ui.alarm

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationService {
    @POST("api/notifications/comments")
    fun sendCommentNotification(   // 댓글
        @Body notificationRequest: NotificationRequest
    ): Call<NotificationResponse>

    @POST("api/notifications/button")
    fun sendButtonNotification(    // 허위,확앴어어요!!
        @Body buttonNotificationRequest: ButtonNotificationRequest
    ): Call<NotificationResponse>

    @POST("api/notifications/nearby/popular")
    fun sendNearbyPopularNotification(
        @Body nearbyPopularNotificationRequest: NearbyPopularNotificationRequest
    ): Call<NotificationResponse>
    @POST("api/notifications/status")
    fun sendStatusNotification(
        @Body statusNotificationRequest: StatusNotificationRequest
    ): Call<NotificationResponse>
}

data class NotificationRequest(
    val postId: Int,
    val commentId: Int,
    val commenterName: String,
    val content: String,
    val isRead: Boolean
)
data class ButtonNotificationRequest(
    val postId: Int,
    val buttonType: String,
    val isRead: Boolean
)
data class NearbyPopularNotificationRequest(
    val postId: Int,
    val title: String,
    val postCreatedAt: String,
    val isRead: Boolean
)
data class StatusNotificationRequest(
    val postId: Int,
    val title: String,
    val expiryTime: String,
    val isRead: Boolean
)

data class NotificationResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: NotificationResult
)

data class NotificationResult(
    val notificationId: Int
)

