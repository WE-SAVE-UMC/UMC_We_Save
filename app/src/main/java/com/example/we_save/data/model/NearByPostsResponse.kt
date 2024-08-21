package com.example.we_save.data.model

import android.os.Parcel
import android.os.Parcelable
import com.example.we_save.common.Constants
import com.google.gson.annotations.SerializedName

data class NearByPostsResponse(
    val result: NearByPostsResult,
) : BaseResponse()

data class NearByPostsResult(
    val userRegionName: String,
    @SerializedName("postDTOs")
    val postDTOs: List<PostDto>,
)

data class PostDto(
    val postId: Long,
    val category: String,
    val title: String,
    val content: String,
    val postRegionName: String,
    val hearts: Long,
    val dislikes: Long,
    val image: String?,
    val imageCount: Long,
    val createdAt: String,
    val distance: String? = null,
    val completed: Boolean,
) : Parcelable {
    val url: String?
        get() = if (image?.isNotBlank() == true) "${Constants.IMAGE_URL_PREFIX}$image" else null

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(postId)
        parcel.writeString(category)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(postRegionName)
        parcel.writeLong(hearts)
        parcel.writeLong(dislikes)
        parcel.writeString(image)
        parcel.writeLong(imageCount)
        parcel.writeString(createdAt)
        parcel.writeString(distance)
        parcel.writeByte(if (completed) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostDto> {
        override fun createFromParcel(parcel: Parcel): PostDto {
            return PostDto(parcel)
        }

        override fun newArray(size: Int): Array<PostDto?> {
            return arrayOfNulls(size)
        }
    }
}
