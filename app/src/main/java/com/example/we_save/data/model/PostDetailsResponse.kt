package com.example.we_save.data.model

import android.os.Parcel
import android.os.Parcelable
import com.example.we_save.common.Constants
import com.google.gson.annotations.Expose

data class PostDetailsResponse(
    val result: PostDetails?,
) : BaseResponse()

data class PostDetails(
    val id: Long,
    val userId: Long,
    val nickname: String,
    val profileImage: String?,
    val status: String,
    val category: String,
    val title: String,
    val content: String,
    val longitude: Double,
    val latitude: Double,
    val postRegionName: String,
    val userReaction: Boolean?,
    val hearts: Long,
    val dislikes: Long,
    val comments: Long,
    val imageCount: Long,
    val createdAt: String,
    val updatedAt: String,
    val images: List<String>,
    val commentsList: List<PostComment>,
) : Parcelable {
    val avatar: String?
        get() = if (profileImage?.isNotBlank() == true) "${Constants.IMAGE_URL_PREFIX}$profileImage" else null

    val urls: List<String>
        get() = if (images.isEmpty()) listOf() else images.map { "${Constants.IMAGE_URL_PREFIX}$it" }

    val postDto: PostDto
        get() = PostDto(
            id,
            category,
            title,
            content,
            postRegionName.split(" ").let {
                it.subList(2, it.count())
            }.joinToString(" "),
            hearts,
            dislikes,
            images.firstOrNull(),
            images.count().toLong(),
            createdAt,
            null,
            status == "COMPLETED",
        )

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(userId)
        parcel.writeString(nickname)
        parcel.writeString(profileImage)
        parcel.writeString(status)
        parcel.writeString(category)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeString(postRegionName)
        parcel.writeValue(userReaction)
        parcel.writeLong(hearts)
        parcel.writeLong(dislikes)
        parcel.writeLong(comments)
        parcel.writeLong(imageCount)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeStringList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostDetails> {
        override fun createFromParcel(parcel: Parcel): PostDetails {
            return PostDetails(parcel)
        }

        override fun newArray(size: Int): Array<PostDetails?> {
            return arrayOfNulls(size)
        }
    }
}

data class PostComment(
    val id: Long,
    val userId: Long,
    val nickname: String,
    val profileImage: String?,
    val content: String,
    val images: List<String>,
    val createdAt: String,
    val updatedAt: String,
) {
    val avatar: String?
        get() = if (profileImage?.isNotBlank() == true) "${Constants.IMAGE_URL_PREFIX}$profileImage" else null

    val urls: List<String>
        get() = if (images.isEmpty()) listOf() else images.map { "${Constants.IMAGE_URL_PREFIX}$it" }

    @Expose
    var isEditing: Boolean = false
}
