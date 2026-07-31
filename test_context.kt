package com.example.ui.screens
import android.content.Context
import android.content.ContextWrapper
import android.app.Activity

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
