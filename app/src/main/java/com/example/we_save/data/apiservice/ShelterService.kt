package com.example.we_save.data.apiservice

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface ShelterService {
    @GET("getTsunamiShelter4List")
    suspend fun getTsunamiShelterList(
        @Query("ServiceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("type") type: String = "json"
    ): TsunamiShelterResponse
}

data class TsunamiShelterResponse(
    @SerializedName("TsunamiShelter")
    val tsunamiShelter: List<ShelterData>
)

data class ShelterData(
    @SerializedName("row")
    val rows: List<ShelterRow>
)

data class ShelterRow(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("shel_nm")
    val name: String,
    @SerializedName("new_address")
    val address: String
)