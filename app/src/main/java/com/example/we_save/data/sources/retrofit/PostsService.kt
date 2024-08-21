package com.example.we_save.data.sources.retrofit

import com.example.we_save.data.model.BaseResponse
import com.example.we_save.data.model.CommentResponse
import com.example.we_save.data.model.DomesticPostsResponse
import com.example.we_save.data.model.NearByPostsResponse
import com.example.we_save.data.model.PostDetailsResponse
import com.example.we_save.data.model.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostsService {
    @Multipart
    @POST("api/posts")
    suspend fun post(
        @Header("Authorization") token: String,
        @Part("postRequestDto") request: RequestBody,
        @Part images: List<MultipartBody.Part>,
    ): Response<PostResponse>

    @Multipart
    @PUT("api/posts/{postId}")
    suspend fun edit(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
        @Part("postRequestDto") request: RequestBody,
        @Part images: List<MultipartBody.Part>,
    ): Response<PostResponse>

    @DELETE("api/posts/{postId}")
    suspend fun remove(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
    ): Response<PostResponse>

    @POST("api/posts/{postId}/report")
    suspend fun report(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
    ): Response<BaseResponse>

    @GET("api/posts/{postId}")
    suspend fun getDetails(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
    ): Response<PostDetailsResponse>

    @Multipart
    @POST("api/posts/comments")
    suspend fun sendComment(
        @Header("Authorization") token: String,
        @Part("commentRequestDto") request: RequestBody,
        @Part images: List<MultipartBody.Part>,
    ): Response<CommentResponse>

    @Multipart
    @PUT("api/posts/comments/{commentId}")
    suspend fun editComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long,
        @Part("commentRequestDto") request: RequestBody,
        @Part images: List<MultipartBody.Part>,
    ): Response<CommentResponse>

    @DELETE("api/posts/comments/{commentId}")
    suspend fun removeComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long,
    ): Response<CommentResponse>

    @POST("api/posts/comments/{commentId}/report")
    suspend fun reportComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long,
    ): Response<BaseResponse>

    @POST("api/posts/{postId}/heart")
    suspend fun like(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
    ): Response<BaseResponse>

    @POST("api/posts/{postId}/dislike")
    suspend fun dislike(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
    ): Response<BaseResponse>

    /**
     * @param filter recent, distance or top
     */
    @POST("api/posts/nearby/{filter}")
    suspend fun getNearByPosts(
        @Path("filter") filter: String,
        @Query("page") page: Int,
        @Query("excludeCompleted") excludeCompleted: Boolean,
        @Body request: PostsRequest
    ): Response<NearByPostsResponse>

    /**
     * @param filter recent or top
     */
    @POST("api/posts/domestic/{filter}")
    suspend fun getDomesticPosts(
        @Path("filter") filter: String,
        @Query("page") page: Int,
        @Query("excludeCompleted") excludeCompleted: Boolean,
    ): Response<DomesticPostsResponse>

    /**
     * @param filter recent, distance or top
     */
    @POST("api/posts/search/nearby/{filter}")
    suspend fun searchNearByPosts(
        @Path("filter") filter: String,
        @Query("page") page: Int,
        @Query("excludeCompleted") excludeCompleted: Boolean,
        @Query("query") query: String,
        @Body request: PostsRequest
    ): Response<NearByPostsResponse>

    /**
     * @param filter recent, distance or top
     */
    @POST("api/posts/search/domestic/{filter}")
    suspend fun searchDomesticPosts(
        @Path("filter") filter: String,
        @Query("page") page: Int,
        @Query("excludeCompleted") excludeCompleted: Boolean,
        @Query("query") query: String,
    ): Response<DomesticPostsResponse>

    @PUT("api/posts/status/{postId}")
    suspend fun complete(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long,
    ): Response<BaseResponse>

    @POST("api/users/block")
    suspend fun block(
        @Header("Authorization") token: String,
        @Body request: BlockRequest,
    ): Response<BaseResponse>
}

data class PostsRequest(
    val latitude: String,
    val longitude: String,
    val regionName: String,
)

data class PostRequest(
    val category: String?,
    val title: String,
    val content: String,
    val status: String,
    val longitude: String?,
    val latitude: String?,
    val postRegionName: String?,
    val report119: Boolean
)

data class CommentRequest(
    val postId: String,
    val content: String,
)

data class BlockRequest(
    val targetId: Long,
)
