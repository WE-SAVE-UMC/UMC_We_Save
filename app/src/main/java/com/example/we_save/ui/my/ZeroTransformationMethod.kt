package com.example.we_save.ui.my

import android.text.method.PasswordTransformationMethod
import android.view.View

class ZeroTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return ZeroCharSequence(source)
    }

    private class ZeroCharSequence(private val source: CharSequence) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char {
            return '●' // 모든 문자에 대해 '●'을 반환
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return ZeroCharSequence(source.subSequence(startIndex, endIndex))
        }
    }
}