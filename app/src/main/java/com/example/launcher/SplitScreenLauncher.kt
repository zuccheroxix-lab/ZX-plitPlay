package com.example.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper

/**
 * Hasil dari percobaan peluncuran split-screen.
 */
sealed class LaunchResult {
    data class Dispatched(
        val gamePackage: String,
        val secondAppPackage: String,
        val message: String,
        val requiresManualAssistance: Boolean
    ) : LaunchResult()

    data class Error(
        val reason: String,
        val exception: Throwable? = null
    ) : LaunchResult()
}

/**
 * Peluncur aplikasi dengan API resmi Android multi-window dan penanganan kegagalan.
 */
class SplitScreenLauncher(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    /**
     * Mencoba meluncurkan pasangan game dan aplikasi kedua menggunakan bendera sistem resmi:
     * FLAG_ACTIVITY_LAUNCH_ADJACENT, FLAG_ACTIVITY_NEW_TASK, dan FLAG_ACTIVITY_MULTIPLE_TASK.
     */
    fun launchSplitPair(
        gamePackage: String,
        secondAppPackage: String,
        onLaunched: (LaunchResult) -> Unit
    ) {
        if (gamePackage.isBlank() || secondAppPackage.isBlank()) {
            onLaunched(
                LaunchResult.Error("Harap pilih Game dan Aplikasi Kedua terlebih dahulu sebelum meluncurkan.")
            )
            return
        }

        if (gamePackage == secondAppPackage) {
            onLaunched(
                LaunchResult.Error("Game dan Aplikasi Kedua tidak boleh berupa aplikasi yang sama.")
            )
            return
        }

        val gameIntent = try {
            packageManager.getLaunchIntentForPackage(gamePackage)
        } catch (e: Exception) {
            null
        }

        if (gameIntent == null) {
            onLaunched(
                LaunchResult.Error("Tidak dapat menemukan aktivitas pembuka untuk game ($gamePackage). Mungkin dinonaktifkan atau dihapus.")
            )
            return
        }

        val secondAppIntent = try {
            packageManager.getLaunchIntentForPackage(secondAppPackage)
        } catch (e: Exception) {
            null
        }

        if (secondAppIntent == null) {
            onLaunched(
                LaunchResult.Error("Tidak dapat menemukan aktivitas pembuka untuk aplikasi kedua ($secondAppPackage).")
            )
            return
        }

        // Siapkan intent game
        gameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // Siapkan intent aplikasi kedua dengan FLAG_ACTIVITY_LAUNCH_ADJACENT
        secondAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            secondAppIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
            secondAppIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }

        try {
            // 1. Luncurkan game
            context.startActivity(gameIntent)

            // 2. Luncurkan aplikasi kedua setelah jeda singkat agar sistem mengatur jendela utama
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    context.startActivity(secondAppIntent)
                    onLaunched(
                        LaunchResult.Dispatched(
                            gamePackage = gamePackage,
                            secondAppPackage = secondAppPackage,
                            message = "Sinyal peluncuran telah dikirimkan ke sistem Android. Pada perangkat yang mendukung peluncuran berdampingan otomatis, layar akan terbagi. Jika OEM Anda membatasinya, gunakan tombol di panel mengambang atau layar Recents untuk memilih aplikasi kedua.",
                            requiresManualAssistance = true
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    onLaunched(LaunchResult.Error("Aplikasi kedua tidak ditemukan saat peluncuran.", e))
                } catch (e: SecurityException) {
                    onLaunched(LaunchResult.Error("Izin ditolak oleh sistem saat membuka aplikasi kedua.", e))
                } catch (e: Exception) {
                    onLaunched(LaunchResult.Error("Gagal membuka aplikasi kedua: ${e.localizedMessage}", e))
                }
            }, 600)

        } catch (e: ActivityNotFoundException) {
            onLaunched(LaunchResult.Error("Game tidak ditemukan atau aktivitas peluncur tidak terdaftar.", e))
        } catch (e: SecurityException) {
            onLaunched(LaunchResult.Error("Izin keamanan sistem menolak peluncuran game.", e))
        } catch (e: Exception) {
            onLaunched(LaunchResult.Error("Gagal meluncurkan game: ${e.localizedMessage}", e))
        }
    }

    /**
     * Membuka satu aplikasi tertentu secara langsung.
     */
    fun launchSingleApp(packageName: String): Boolean {
        if (packageName.isBlank()) return false
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
}
