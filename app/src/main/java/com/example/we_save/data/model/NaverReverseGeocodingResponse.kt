package com.example.we_save.data.model

import android.util.Log
import com.example.we_save.data.sources.retrofit.PostsRequest

typealias ReverseGeocoding = NaverReverseGeocodingResponse

data class NaverReverseGeocodingResponse(
    val status: NaverReverseGeocodingStatus,
    val results: List<NaverReverseGeocodingResult>,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
) {
    val region0: String
        get() = results.first().region.area0.name

    val region1: String
        get() = results.first().region.area1.name

    val region2: String
        get() = results.first().region.area2.name

    val region3: String
        get() = results.first().region.area3.name

    val region4: String
        get() = results.first().region.area4.name

    val landType: String?
        get() = results.first().land?.type

    val number1: String?
        get() = results.first().land?.number1

    val number2: String?
        get() = results.first().land?.number2

    val api: String
        get() = "$region1 ${region2.replace(" ", "")} ${region3}${region4}".trim().let {
            val numbers = "${
                listOfNotNull(number1, number2).filter { it.isNotBlank() }.joinToString("-")
            }".trim()

            if (numbers.isNotBlank()) {
                if (landType == "1") {
                    return@let "$it $numbers"
                } else if (landType == "2") {
                    return@let "$it 산 $numbers"
                }
            }

            return@let it
        }

    val short: String
        get() = if (region4.isBlank()) "$region2 $region3" else "$region3 $region4".trim()

    val address: String
        get() = "$region1 $region2 $region3 $region4".trim().let {
            val numbers = "${
                listOfNotNull(number1, number2).filter { it.isNotBlank() }.joinToString("-")
            }".trim()

            if (numbers.isNotBlank()) {
                if (landType == "1") {
                    return@let "$it $numbers"
                } else if (landType == "2") {
                    return@let "$it 산 $numbers"
                }
            }

            return@let it
        }

    val postsRequest: PostsRequest
        get() = PostsRequest(latitude.toString(), longitude.toString(), api)
}

data class NaverReverseGeocodingStatus(
    val code: Long,
    val name: String,
    val message: String,
)

data class NaverReverseGeocodingResult(
    val name: String,
    val code: Code,
    val region: Region,
    val land: Land?,
)

data class Code(
    val id: String,
    val type: String,
    val mappingId: String,
)

data class Region(
    val area0: Area0,
    val area1: Area1,
    val area2: Area2,
    val area3: Area3,
    val area4: Area4,
)

data class Area0(
    val name: String,
    val coords: Coords,
)

data class Coords(
    val center: Center,
)

data class Center(
    val crs: String,
    val x: Double,
    val y: Double,
)

data class Area1(
    val name: String,
    val coords: Coords2,
    val alias: String,
)

data class Coords2(
    val center: Center2,
)

data class Center2(
    val crs: String,
    val x: Double,
    val y: Double,
)

data class Area2(
    val name: String,
    val coords: Coords3,
)

data class Coords3(
    val center: Center3,
)

data class Center3(
    val crs: String,
    val x: Double,
    val y: Double,
)

data class Area3(
    val name: String,
    val coords: Coords4,
)

data class Coords4(
    val center: Center4,
)

data class Center4(
    val crs: String,
    val x: Double,
    val y: Double,
)

data class Area4(
    val name: String,
    val coords: Coords5,
)

data class Coords5(
    val center: Center5,
)

data class Center5(
    val crs: String,
    val x: Double,
    val y: Double,
)

data class Land(
    val type: String,
    val number1: String,
    val number2: String,
    val addition0: Addition0,
    val addition1: Addition1,
    val addition2: Addition2,
    val addition3: Addition3,
    val addition4: Addition4,
    val coords: Coords6,
)

data class Addition0(
    val type: String,
    val value: String,
)

data class Addition1(
    val type: String,
    val value: String,
)

data class Addition2(
    val type: String,
    val value: String,
)

data class Addition3(
    val type: String,
    val value: String,
)

data class Addition4(
    val type: String,
    val value: String,
)

data class Coords6(
    val center: Center6,
)

data class Center6(
    val crs: String,
    val x: Double,
    val y: Double,
)
