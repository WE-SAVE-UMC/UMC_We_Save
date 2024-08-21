package com.example.we_save.data.apiservice

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface MyCommentInterface {

    @GET("/api/users/comments")
    fun getComments(@Header("Authorization") token: String): Call<CommentResponse>

    // 포스터 아이디로 게시글 보기
    @GET("/api/posts/{postId}")
    fun getPost(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int): Call<PostResponse>

}