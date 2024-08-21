package com.example.we_save.ui.search

import android.os.Parcel
import android.os.Parcelable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeSearchService {
    @GET("/api/home/search")
    fun searchTags(
        @Query("tag") tag: String
    ): Call<SearchResponse>
}

data class SearchResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: SearchResult
)

data class SearchResult(
    val tags: List<String>,
    val countermeasureDtos: List<CountermeasureDto>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList() ?: emptyList(),
        parcel.createTypedArrayList(CountermeasureDto.CREATOR) ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(tags)
        parcel.writeTypedList(countermeasureDtos)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SearchResult> {
        override fun createFromParcel(parcel: Parcel): SearchResult {
            return SearchResult(parcel)
        }

        override fun newArray(size: Int): Array<SearchResult?> {
            return arrayOfNulls(size)
        }
    }
}

data class CountermeasureDto(
    val title: String,
    val mainContent: String,
    val detailContent: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(mainContent)
        parcel.writeString(detailContent)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CountermeasureDto> {
        override fun createFromParcel(parcel: Parcel): CountermeasureDto {
            return CountermeasureDto(parcel)
        }

        override fun newArray(size: Int): Array<CountermeasureDto?> {
            return arrayOfNulls(size)
        }
    }
}