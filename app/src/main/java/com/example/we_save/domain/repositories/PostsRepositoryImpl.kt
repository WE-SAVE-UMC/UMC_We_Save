package com.example.we_save.domain.repositories

import android.content.Context
import com.example.we_save.data.model.BaseResponse
import com.example.we_save.data.model.CommentResponse
import com.example.we_save.data.model.PostDetailsResponse
import com.example.we_save.data.model.PostResponse
import com.example.we_save.data.sources.PostsRemoteSource
import com.example.we_save.data.sources.retrofit.CommentRequest
import com.example.we_save.data.sources.retrofit.PostRequest
import com.example.we_save.data.sources.retrofit.PostsRequest
import retrofit2.Response

class PostsRepositoryImpl private constructor(context: Context) : PostsRepository {
    companion object {
        private var instance: PostsRepository? = null

        fun getInstance(context: Context): PostsRepository {
            return instance ?: PostsRepositoryImpl(context).also {
                instance = it
            }
        }
    }

    private val source by lazy { PostsRemoteSource.getInstance(context) }

    override val myUserId: Long
        get() = source.userId

    override suspend fun post(request: PostRequest, images: List<String>): Response<PostResponse> =
        source.post(request, images)

    override suspend fun edit(
        postId: Long,
        request: PostRequest,
        images: List<String>
    ): Response<PostResponse> = source.edit(postId, request, images)

    override suspend fun remove(postId: Long): Response<PostResponse> = source.remove(postId)

    override suspend fun report(postId: Long): Response<BaseResponse> = source.report(postId)

    override suspend fun getDetails(postId: Long): Response<PostDetailsResponse> =
        source.getDetails(postId)

    override suspend fun sendComment(
        request: CommentRequest,
        images: List<String>
    ): Response<CommentResponse> = source.sendComment(request, images)

    override suspend fun editComment(
        commentId: Long,
        request: CommentRequest,
        images: List<String>
    ): Response<CommentResponse> = source.editComment(commentId, request, images)

    override suspend fun removeComment(commentId: Long): Response<CommentResponse> =
        source.removeComment(commentId)

    override suspend fun reportComment(commentId: Long): Response<BaseResponse> =
        source.reportComment(commentId)

    override suspend fun like(postId: Long): Response<BaseResponse> = source.like(postId)

    override suspend fun dislike(postId: Long): Response<BaseResponse> = source.dislike(postId)

    override suspend fun getNearByPosts(
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
        request: PostsRequest
    ) = source.getNearByPosts(filter, page, excludeCompleted, request)

    override suspend fun getDomesticPosts(
        filter: String,
        page: Int,
        excludeCompleted: Boolean
    ) = source.getDomesticPosts(filter, page, excludeCompleted)

    override suspend fun searchNearByPosts(
        query: String,
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
        request: PostsRequest
    ) = source.searchNearByPosts(query, filter, page, excludeCompleted, request)

    override suspend fun searchDomesticPosts(
        query: String,
        filter: String,
        page: Int,
        excludeCompleted: Boolean
    ) = source.searchDomesticPosts(query, filter, page, excludeCompleted)

    override suspend fun complete(postId: Long): Response<BaseResponse> = source.complete(postId)

    override suspend fun block(userId: Long): Response<BaseResponse> = source.block(userId)

    override val temporaryPost: Pair<PostRequest, List<String>>?
        get() = source.temporaryPost

    override suspend fun temporarySave(request: PostRequest?, images: List<String>) =
        source.temporarySave(request, images)
}