package com.example.we_save

import android.graphics.Paint
import android.text.style.LineHeightSpan

class CustomLineHeightSpan (private val height: Int) : LineHeightSpan {
    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt?
    ) {
        fm?.let {
            val originalHeight = it.descent - it.ascent
            if (originalHeight > 0) {
                val ratio = height.toFloat() / originalHeight
                it.descent = Math.round(it.descent * ratio)
                it.ascent = it.descent - height
            }
        }
    }

}