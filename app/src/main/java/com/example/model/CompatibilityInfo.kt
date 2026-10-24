package com.example.model

/**
 * Tingkat dukungan fitur pada perangkat Android.
 */
enum class SupportLevel {
    SUPPORTED,                 // Didukung penuh
    SUPPORTED_WITH_LIMITS,      // Didukung dengan batasan OEM/OS
    PERMISSION_REQUIRED,       // Memerlukan izin pengguna
    NOT_SUPPORTED,             // Tidak didukung oleh sistem
    UNVERIFIED                 // Belum dapat diverifikasi
}

/**
 * Item hasil pemeriksaan kompatibilitas sistem.
 */
data class CompatibilityItem(
    val id: String,
    val title: String,
    val description: String,
    val level: SupportLevel,
    val detail: String
)

/**
 * Laporan ringkas seluruh kondisi kompatibilitas perangkat.
 */
data class DeviceReport(
    val androidRelease: String,
    val apiLevel: Int,
    val manufacturer: String,
    val model: String,
    val isMultiWindowSupported: Boolean,
    val hasOverlayPermission: Boolean,
    val isPictureInPictureSupported: Boolean,
    val isFreeformSupported: Boolean,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val items: List<CompatibilityItem>
)
