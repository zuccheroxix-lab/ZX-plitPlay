package com.example.compatibility

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.model.CompatibilityItem
import com.example.model.DeviceReport
import com.example.model.SupportLevel

/**
 * Pemeriksa kompatibilitas nyata perangkat Android menggunakan API sistem resmi.
 */
class DeviceCompatibilityChecker(private val context: Context) {

    fun generateReport(): DeviceReport {
        val pm = context.packageManager
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

        // 1. Cek Versi Android & API Level
        val apiLevel = Build.VERSION.SDK_INT
        val release = Build.VERSION.RELEASE
        val apiItem = if (apiLevel >= Build.VERSION_CODES.N) {
            CompatibilityItem(
                id = "android_version",
                title = "Versi Android (API $apiLevel)",
                description = "Android $release terdeteksi. Multi-window API resmi tersedia sejak Android 7.0 (API 24).",
                level = SupportLevel.SUPPORTED,
                detail = "Build: ${Build.DISPLAY}"
            )
        } else {
            CompatibilityItem(
                id = "android_version",
                title = "Versi Android (API $apiLevel)",
                description = "Android $release berada di bawah API 24. Split-screen multi-window bawaan tidak didukung.",
                level = SupportLevel.NOT_SUPPORTED,
                detail = "Memerlukan minimal Android 7.0 (Nougat)"
            )
        }

        // 2. Cek Dukungan Multi-Window Sistem
        val isLowRam = am?.isLowRamDevice == true
        val isMultiWindowSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0+ mendukung multi-window AOSP kecuali dibatasi pada profil Android Go
            !isLowRam || pm.hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)
        } else {
            false
        }

        val multiWindowItem = if (isMultiWindowSupported) {
            CompatibilityItem(
                id = "multi_window",
                title = "Dukungan Multi-Window Sistem",
                description = "Sistem operasi Android mendukung pembagian layar (split-screen).",
                level = SupportLevel.SUPPORTED,
                detail = "AOSP Multi-Window Framework Aktif (Low-RAM: $isLowRam)"
            )
        } else {
            CompatibilityItem(
                id = "multi_window",
                title = "Dukungan Multi-Window Sistem",
                description = "Sistem atau ROM perangkat ini membatasi fitur multi-window (umum pada profil perangkat RAM sangat rendah / Android Go).",
                level = SupportLevel.NOT_SUPPORTED,
                detail = "Perangkat Low-RAM terdeteksi membatasi split-screen"
            )
        }

        // 3. Cek Izin Overlay (SYSTEM_ALERT_WINDOW)
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }

        val overlayItem = if (hasOverlay) {
            CompatibilityItem(
                id = "overlay_permission",
                title = "Izin Floating Overlay",
                description = "Izin menampilkan jendela di atas aplikasi lain telah aktif.",
                level = SupportLevel.SUPPORTED,
                detail = "Settings.canDrawOverlays = true"
            )
        } else {
            CompatibilityItem(
                id = "overlay_permission",
                title = "Izin Floating Overlay",
                description = "Panel kontrol mengambang memerlukan izin 'Tampilkan di atas aplikasi lain'.",
                level = SupportLevel.PERMISSION_REQUIRED,
                detail = "Izin belum diberikan di Pengaturan Sistem"
            )
        }

        // 4. Cek Picture-in-Picture
        val isPipSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else {
            false
        }

        val pipItem = if (isPipSupported) {
            CompatibilityItem(
                id = "pip_support",
                title = "Picture-in-Picture (PiP)",
                description = "Didukung untuk panel asisten mini ZX SplitPlay.",
                level = SupportLevel.SUPPORTED,
                detail = "FEATURE_PICTURE_IN_PICTURE = true"
            )
        } else {
            CompatibilityItem(
                id = "pip_support",
                title = "Picture-in-Picture (PiP)",
                description = "Hardware atau konfigurasi sistem tidak mengaktifkan Picture-in-Picture.",
                level = SupportLevel.SUPPORTED_WITH_LIMITS,
                detail = "FEATURE_PICTURE_IN_PICTURE = false"
            )
        }

        // 5. Cek Freeform Window Management
        val isFreeformSupported = pm.hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)
        val freeformItem = if (isFreeformSupported) {
            CompatibilityItem(
                id = "freeform_support",
                title = "Jendela Bebas (Freeform Window)",
                description = "Perangkat mendukung mode multi-window bebas (tablet/desktop mode).",
                level = SupportLevel.SUPPORTED,
                detail = "FEATURE_FREEFORM_WINDOW_MANAGEMENT = true"
            )
        } else {
            CompatibilityItem(
                id = "freeform_support",
                title = "Jendela Bebas (Freeform Window)",
                description = "Mode standar split-screen 50:50 aktif. Jendela bebas floating memerlukan opsi pengembang OEM.",
                level = SupportLevel.SUPPORTED_WITH_LIMITS,
                detail = "Dapat diaktifkan via Opsi Pengembang pada sebagian besar perangkat"
            )
        }

        // 6. Cek RAM & Memori Perangkat
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        val totalMb = memInfo.totalMem / (1024 * 1024)
        val availMb = memInfo.availMem / (1024 * 1024)

        val ramItem = if (totalMb >= 3800) {
            CompatibilityItem(
                id = "device_ram",
                title = "Kapasitas RAM (${totalMb / 1024} GB)",
                description = "Kapasitas RAM memadai untuk menjalankan game dan aplikasi pendamping secara bersamaan.",
                level = SupportLevel.SUPPORTED,
                detail = "Tersedia: ${availMb} MB / Total: ${totalMb} MB"
            )
        } else if (totalMb >= 2000) {
            CompatibilityItem(
                id = "device_ram",
                title = "Kapasitas RAM (${totalMb / 1024} GB)",
                description = "Kapasitas sedang. Game berat dapat mengalami reload jika aplikasi kedua membutuhkan banyak memori.",
                level = SupportLevel.SUPPORTED_WITH_LIMITS,
                detail = "Tersedia: ${availMb} MB / Total: ${totalMb} MB"
            )
        } else {
            CompatibilityItem(
                id = "device_ram",
                title = "Kapasitas RAM (${totalMb / 1024} GB)",
                description = "RAM terbatas. Split-screen mungkin sering menutup aplikasi latar belakang oleh manajemen memori agresif.",
                level = SupportLevel.SUPPORTED_WITH_LIMITS,
                detail = "Tersedia: ${availMb} MB / Total: ${totalMb} MB"
            )
        }

        // 7. Catatan OEM
        val mfr = Build.MANUFACTURER.lowercase()
        val oemDetail = when {
            mfr.contains("xiaomi") || mfr.contains("redmi") || mfr.contains("poco") ->
                "Xiaomi/MIUI/HyperOS: Jika tombol split di Recents tersembunyi, gunakan 'Jendela Mengambang' atau aktifkan 'Paksa aktivitas dapat diubah ukuran' di Opsi Pengembang."
            mfr.contains("samsung") ->
                "Samsung One UI: Buka Recents > ketuk ikon game di atas kartu > pilih 'Buka dalam tampilan layar pisah'. Dukungan Samsung Labs juga tersedia."
            mfr.contains("oppo") || mfr.contains("realme") || mfr.contains("oneplus") ->
                "ColorOS/Realme UI: Geser ke atas dengan 3 jari pada game untuk langsung membuka menu aplikasi kedua, atau gunakan Smart Sidebar."
            mfr.contains("vivo") || mfr.contains("iqoo") ->
                "FuntouchOS/OriginOS: Geser ke bawah dengan 3 jari untuk Smart Split."
            else ->
                "AOSP / Stock Android: Buka layar Recents (aplikasi terbaru), ketuk ikon aplikasi di atas kartu pratinjau, lalu pilih 'Layar Pisah' (Split Screen)."
        }

        val oemItem = CompatibilityItem(
            id = "oem_guidance",
            title = "Karakteristik OEM (${Build.MANUFACTURER.uppercase()})",
            description = oemDetail,
            level = SupportLevel.SUPPORTED,
            detail = "Model: ${Build.MODEL} (${Build.DEVICE})"
        )

        val items = listOf(apiItem, multiWindowItem, overlayItem, ramItem, pipItem, freeformItem, oemItem)

        return DeviceReport(
            androidRelease = release,
            apiLevel = apiLevel,
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            isMultiWindowSupported = isMultiWindowSupported,
            hasOverlayPermission = hasOverlay,
            isPictureInPictureSupported = isPipSupported,
            isFreeformSupported = isFreeformSupported,
            totalRamMb = totalMb,
            availableRamMb = availMb,
            items = items
        )
    }
}
