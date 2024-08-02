package com.example.we_save.common.extensions

import androidx.fragment.app.FragmentTransaction
import com.example.we_save.R

fun FragmentTransaction.setAppAnimation(): FragmentTransaction {
    setCustomAnimations(
        R.anim.slide_in,    // enter
        R.anim.fade_out,    // exit
        R.anim.fade_in,     // popEnter
        R.anim.slide_out    // popExit
    )

    return this
}