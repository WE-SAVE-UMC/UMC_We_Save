package com.example.we_save.domain.repositories

import com.example.we_save.data.model.BaseResponse
import com.example.we_save.data.model.CommentResponse
import com.example.we_save.data.model.DomesticPostsResponse
import com.example.we_save.data.model.NearByPostsResponse
import com.example.we_save.data.model.PostDetailsResponse
import com.example.we_save.data.model.PostResponse
import com.example.we_save.data.sources.retrofit.CommentRequest
import com.example.we_save.data.sources.retrofit.PostRequest
import com.example.we_save.data.sources.retrofit.PostsRequest
import retrofit2.Response

interface PostsRepository {
    val myUserId: Long

    /**
     * 사건/사고 등록
     */
    suspend fun post(request: PostRequest, images: List<String>): Response<PostResponse>

    /**
     * 사건/사고 수정
     */
    suspend fun edit(
        postId: Long,
        request: PostRequest,
        images: List<String>
    ): Response<PostResponse>

    /**
     * 사건/사고 삭제
     */
    suspend fun remove(
        postId: Long,
    ): Response<PostResponse>

    /**
     * 사건/사고 게시글 신고
     */
    suspend fun report(
        postId: Long,
    ): Response<BaseResponse>

    /**
     * 사건/사고 게시글 보기
     */
    suspend fun getDetails(
        postId: Long,
    ): Response<PostDetailsResponse>

    /**
     * 댓글 작성
     */
    suspend fun sendComment(
        request: CommentRequest,
        images: List<String>
    ): Response<CommentResponse>

    /**
     * 댓글 수정
     */
    suspend fun editComment(
        commentId: Long,
        request: CommentRequest,
        images: List<String>
    ): Response<CommentResponse>

    /**
     * 댓글 삭제
     */
    suspend fun removeComment(
        commentId: Long,
    ): Response<CommentResponse>

    /**
     * 댓글 신고
     */
    suspend fun reportComment(
        commentId: Long,
    ): Response<BaseResponse>

    /**
     * 확인했어요 등록/취소
     */
    suspend fun like(
        postId: Long,
    ): Response<BaseResponse>

    /**
     * 허위에요 등록/취소
     */
    suspend fun dislike(
        postId: Long,
    ): Response<BaseResponse>

    suspend fun getNearByPosts(
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
        request: PostsRequest
    ): Response<NearByPostsResponse>

    suspend fun getDomesticPosts(
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
    ): Response<DomesticPostsResponse>

    suspend fun searchNearByPosts(
        query: String,
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
        request: PostsRequest
    ): Response<NearByPostsResponse>

    suspend fun searchDomesticPosts(
        query: String,
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
    ): Response<DomesticPostsResponse>

    /**
     * 게시글 화면에서 상황종료처리
     */
    suspend fun complete(
        postId: Long,
    ): Response<BaseResponse>

    /**
     * 사용자 차단
     */
    suspend fun block(
        userId: Long,
    ): Response<BaseResponse>

    /**
     * 임시 저장된 데이터 가져오기
     */
    val temporaryPost: Pair<PostRequest, List<String>>?

    /**
     * 임시 저장 / 삭제
     */
    suspend fun temporarySave(request: PostRequest? = null, images: List<String> = listOf())
}