package com.example.model

/**
 * Pengaturan lokal pengguna yang tersimpan di perangkat.
 */
data class UserSettings(
    val selectedGamePackage: String = "",
    val selectedGameLabel: String = "",
    val selectedSecondAppPackage: String = "",
    val selectedSecondAppLabel: String = "",
    val floatingOpacity: Float = 0.9f,
    val floatingSizeDp: Int = 56,
    val hapticFeedbackEnabled: Boolean = true,
    val autoOpenFloatingOnSplit: Boolean = true
)
