package com.example.we_save.common.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard(view: View? = null) {
    val v = view ?: this.view
    if (v == null) return

    val inputMethodManager =
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)

    if (v is EditText) {
        v.clearFocus()
    }
}