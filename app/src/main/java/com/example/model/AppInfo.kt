package com.example.model

import android.graphics.drawable.Drawable

/**
 * Representasi aplikasi yang terpasang pada perangkat.
 */
data class AppInfo(
    val packageName: String,
    val label: String,
    val isGame: Boolean,
    val isInstalled: Boolean = true,
    val versionName: String = "",
    val icon: Drawable? = null
)
