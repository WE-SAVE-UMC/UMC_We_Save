package com.example.we_save.data.model

import com.google.gson.annotations.SerializedName

data class KakaoReverseGeocodingResponse(
    val meta: Meta,
    val documents: List<Document>,
)

data class Meta(
    @SerializedName("total_count")
    val totalCount: Long,
)

data class Document(
    @SerializedName("road_address")
    val roadAddress: RoadAddress?,
    val address: Address,
)

data class RoadAddress(
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("region_1depth_name")
    val region1depthName: String,
    @SerializedName("region_2depth_name")
    val region2depthName: String,
    @SerializedName("region_3depth_name")
    val region3depthName: String,
    @SerializedName("road_name")
    val roadName: String,
    @SerializedName("underground_yn")
    val undergroundYn: String,
    @SerializedName("main_building_no")
    val mainBuildingNo: String,
    @SerializedName("sub_building_no")
    val subBuildingNo: String,
    @SerializedName("building_name")
    val buildingName: String,
    @SerializedName("zone_no")
    val zoneNo: String,
)

data class Address(
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("region_1depth_name")
    val region1depthName: String,
    @SerializedName("region_2depth_name")
    val region2depthName: String,
    @SerializedName("region_3depth_name")
    val region3depthName: String,
    @SerializedName("mountain_yn")
    val mountainYn: String,
    @SerializedName("main_address_no")
    val mainAddressNo: String,
    @SerializedName("sub_address_no")
    val subAddressNo: String,
    @SerializedName("zip_code")
    val zipCode: String,
) {
    override fun toString(): String {
        var str = listOf(
            region1depthName,
            region2depthName.replace(" ", ""),
            region3depthName.replace(" ", "")
        ).filter { it.isNotBlank() }
            .joinToString(" ")

        if (mountainYn == "Y") {
            str += " 산"
        }

        if (mainAddressNo.isNotBlank() || subAddressNo.isNotBlank()) {
            str += " ${
                (listOf(mainAddressNo, subAddressNo).filter { it.isNotBlank() }
                    .joinToString("-"))
            }"
        }

        return str
    }
}
