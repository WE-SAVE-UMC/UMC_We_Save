package com.example.we_save.domain.model

enum class AccidentType(val title: String) {
    FIRE("화재"), EARTHQUAKE("지진"), HEAVY_RAIN("폭우"), HEAVY_SNOW("폭설"), TRAFFIC_ACCIDENT("교통사고"), OTHER(
        "기타"
    )
}