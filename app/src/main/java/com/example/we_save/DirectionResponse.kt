package com.example.we_save

data class DirectionResponse(
    val route: Route
)

data class Route(
    val traoptimal: List<Traoptimal>
)

data class Traoptimal(
    val summary: Summary,
    val path: List<List<Double>>
)

data class Summary(
    val duration: Int,
    val distance: Int
)
