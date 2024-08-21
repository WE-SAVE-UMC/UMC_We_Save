package com.example.we_save.common.extensions

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.we_save.databinding.LayoutToastBinding

fun Context.customToast(message: String): Toast {
    val binding = LayoutToastBinding.inflate(LayoutInflater.from(this))
    binding.message.text = message

    val toast = Toast(this).apply {
        duration = Toast.LENGTH_SHORT
        view = binding.root
    }

    return toast
}