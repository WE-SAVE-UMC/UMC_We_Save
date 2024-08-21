package com.example.we_save.data.apiservice

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface MyPostInterface {

    @GET("/api/users/posts")
    fun getPosts(@Header("Authorization") token: String): Call<PostResponse>

    @PUT("/api/users/posts/{postId}")
    fun completePosts(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int): Call<PostResponse>

    @DELETE("/api/users/posts/{postId}")
    fun deletePosts(
        @Header("Authorization") token: String,
        @Path("postId") postId: Int): Call<PostResponse>

}