package com.example.we_save.data.sources

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import androidx.core.content.edit
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.example.we_save.common.Constants
import com.example.we_save.data.model.BaseResponse
import com.example.we_save.data.model.CommentResponse
import com.example.we_save.data.model.PostDetailsResponse
import com.example.we_save.data.model.PostResponse
import com.example.we_save.data.sources.retrofit.BlockRequest
import com.example.we_save.data.sources.retrofit.CommentRequest
import com.example.we_save.data.sources.retrofit.PostRequest
import com.example.we_save.data.sources.retrofit.PostsRequest
import com.example.we_save.data.sources.retrofit.PostsService
import com.example.we_save.data.sources.retrofit.retrofit
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Date


class PostsRemoteSource private constructor(val context: Context) {
    companion object {
        private var instance: PostsRemoteSource? = null

        fun getInstance(context: Context): PostsRemoteSource {
            return instance ?: PostsRemoteSource(context.applicationContext).also {
                instance = it
            }
        }
    }

    private val service by lazy {
        retrofit.create(PostsService::class.java)
    }

    private val prefs by lazy {
        context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    val userId: Long
        get() = prefs.getLong(
            Constants.KEY_USER_ID,
            if (Constants.DEV_MODE) 14L else -1L
        )

    private val token: String?
        get() = prefs.getString(
            Constants.KEY_TOKEN,
            if (Constants.DEV_MODE) "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IjE0Iiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MjQyMDQ4NzcsImV4cCI6MTcyNDI5MTI3N30.6mg4CwhzwJqKFGqJahkXPBlBw1CP8-iV5aZNGaDh_zk" else null
        )

    val temporaryPost: Pair<PostRequest, List<String>>?
        get() {
            val json = prefs.getString(Constants.KEY_TEMPORARY_POST_SAVE, null) ?: return null
            val save = Gson().fromJson<TemporaryPostSave>(json, TemporaryPostSave::class.java)

            return save.request to save.images
        }

    /**
     * 사건/사고 등록
     */
    suspend fun post(request: PostRequest, images: List<String>): Response<PostResponse> =
        withContext(Dispatchers.IO) {
            val dto = Gson().toJson(request)
                .toRequestBody("application/json".toMediaTypeOrNull())
            val parts = imageToMultiParts(images)

            return@withContext service.post(token!!, dto, parts)
        }

    /**
     * 사건/사고 수정
     */
    suspend fun edit(
        postId: Long,
        request: PostRequest,
        images: List<String>
    ): Response<PostResponse> = withContext(Dispatchers.IO) {
        val dto = Gson().toJson(request)
            .toRequestBody("application/json".toMediaTypeOrNull())
        val parts = imageToMultiParts(images)

        return@withContext service.edit(token!!, postId, dto, parts)
    }

    /**
     * 사건/사고 삭제
     */
    suspend fun remove(
        postId: Long,
    ): Response<PostResponse> = withContext(Dispatchers.IO) {
        service.remove(token!!, postId)
    }

    /**
     * 사건/사고 게시글 신고
     */
    suspend fun report(
        postId: Long,
    ): Response<BaseResponse> = withContext(Dispatchers.IO) {
        service.report(token!!, postId)
    }

    /**
     * 사건/사고 게시글 보기
     */
    suspend fun getDetails(
        postId: Long,
    ): Response<PostDetailsResponse> = withContext(Dispatchers.IO) {
        service.getDetails(token!!, postId)
    }

    /**
     * 댓글 작성
     */
    suspend fun sendComment(
        request: CommentRequest,
        images: List<String>
    ): Response<CommentResponse> = withContext(Dispatchers.IO) {
        val dto = Gson().toJson(request)
            .toRequestBody("application/json".toMediaTypeOrNull())
        val parts = imageToMultiParts(images)

        return@withContext service.sendComment(token!!, dto, parts)
    }

    /**
     * 댓글 수정
     */
    suspend fun editComment(
        commentId: Long,
        request: CommentRequest,
        images: List<String>
    ): Response<CommentResponse> = withContext(Dispatchers.IO) {
        val dto = Gson().toJson(request)
            .toRequestBody("application/json".toMediaTypeOrNull())
        val parts = imageToMultiParts(images)

        return@withContext service.editComment(token!!, commentId, dto, parts)
    }

    /**
     * 댓글 삭제
     */
    suspend fun removeComment(
        commentId: Long,
    ): Response<CommentResponse> = withContext(Dispatchers.IO) {
        return@withContext service.removeComment(token!!, commentId)
    }

    /**
     * 댓글 신고
     */
    suspend fun reportComment(
        commentId: Long,
    ): Response<BaseResponse> = withContext(Dispatchers.IO) {
        return@withContext service.reportComment(token!!, commentId)
    }

    /**
     * 확인했어요 등록/취소
     */
    suspend fun like(
        postId: Long,
    ): Response<BaseResponse> = withContext(Dispatchers.IO) {
        return@withContext service.like(token!!, postId)
    }

    /**
     * 허위에요 등록/취소
     */
    suspend fun dislike(
        postId: Long,
    ): Response<BaseResponse> = withContext(Dispatchers.IO) {
        return@withContext service.dislike(token!!, postId)
    }

    suspend fun getNearByPosts(
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
        request: PostsRequest
    ) = withContext(Dispatchers.IO) {
        Log.d("PostsRemoteSource", Gson().toJson(request))

        val source = service.getNearByPosts(
            filter,
            page,
            excludeCompleted,
            request
        )

        return@withContext source
    }

    suspend fun getDomesticPosts(
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
    ) = withContext(Dispatchers.IO) {
        service.getDomesticPosts(
            filter,
            page,
            excludeCompleted,
        )
    }

    suspend fun searchNearByPosts(
        query: String,
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
        request: PostsRequest
    ) = withContext(Dispatchers.IO) {
        Log.d("PostsRemoteSource", Gson().toJson(request))

        service.searchNearByPosts(
            filter,
            page,
            excludeCompleted,
            query,
            request
        )
    }

    suspend fun searchDomesticPosts(
        query: String,
        filter: String,
        page: Int,
        excludeCompleted: Boolean,
    ) = withContext(Dispatchers.IO) {
        service.searchDomesticPosts(
            filter,
            page,
            excludeCompleted,
            query,
        )
    }

    /**
     * 게시글 화면에서 상황종료처리
     */
    suspend fun complete(
        postId: Long,
    ): Response<BaseResponse> = withContext(Dispatchers.IO) {
        return@withContext service.complete(token!!, postId)
    }

    /**
     * 사용자 차단
     */
    suspend fun block(
        userId: Long,
    ): Response<BaseResponse> = withContext(Dispatchers.IO) {
        return@withContext service.block(token!!, BlockRequest(userId))
    }

    /**
     * 사건/사고 임시저장, 삭제
     */
    suspend fun temporarySave(request: PostRequest?, images: List<String> = listOf()) {
        if (request == null) {
            prefs.edit {
                this.remove(Constants.KEY_TEMPORARY_POST_SAVE)
            }

            return
        }

        val save = TemporaryPostSave(request, imageToUrls(images))
        val json = Gson().toJson(save)

        prefs.edit {
            putString(Constants.KEY_TEMPORARY_POST_SAVE, json)
        }
    }
}

private data class TemporaryPostSave(
    val request: PostRequest,
    val images: List<String>,
)

private suspend fun PostsRemoteSource.imageToMultiParts(images: List<String>) =
    withContext(Dispatchers.IO) {
        return@withContext images.mapIndexed { index, url ->
            async {
                if (URLUtil.isNetworkUrl(url)) {
                    val file = Glide.with(context)
                        .downloadOnly()
                        .load(url)
                        .submit()
                        .get()
                    val body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("images", file.name, body)

                } else {
                    try {
                        val uri = Uri.parse(url)!!
                        val file: File

                        if (uri.scheme == "file") {
                            file = uri.toFile()
                        } else {
                            file = File(context.cacheDir, "${Date().time}_${index}.png")
                            file.createNewFile()

                            context.contentResolver.openInputStream(Uri.parse(url))!!.use { ins ->
                                FileOutputStream(file).use { os ->
                                    val buffer = ByteArray(4096)
                                    var length: Int
                                    while ((ins.read(buffer).also { length = it }) > 0) {
                                        os.write(buffer, 0, length)
                                    }
                                    os.flush()
                                }
                            }
                        }

                        val body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images", file.name, body)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        }.awaitAll().filterNotNull()
    }

private suspend fun PostsRemoteSource.imageToUrls(images: List<String>) =
    withContext(Dispatchers.IO) {
        return@withContext images.mapIndexed { index, url ->
            async {
                if (URLUtil.isNetworkUrl(url)) {
                    return@async url
                } else {
                    try {
                        val uri = Uri.parse(url)!!

                        if (uri.scheme == "file") {
                            return@async url
                        } else {
                            val file = File(context.cacheDir, "${Date().time}_${index}.png")
                            file.createNewFile()

                            context.contentResolver.openInputStream(Uri.parse(url))!!.use { ins ->
                                FileOutputStream(file).use { os ->
                                    val buffer = ByteArray(4096)
                                    var length: Int
                                    while ((ins.read(buffer).also { length = it }) > 0) {
                                        os.write(buffer, 0, length)
                                    }
                                    os.flush()
                                }
                            }

                            return@async Uri.fromFile(file).toString()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        }.awaitAll().filterNotNull()
    }
