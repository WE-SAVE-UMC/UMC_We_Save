package com.example.we_save.ui.alarm

data class Alarm(
    val alarmBackground: Int, // 배경 색상
    val alarmOvalBackground: Int, // 원의 색상
    val imageRes: Int, // 이미지 리소스
    val mainText: String, // 메인 알림 텍스트
    val subText: String, // 서브 알림 텍스트
)