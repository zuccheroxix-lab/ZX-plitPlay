package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository untuk memindai aplikasi yang terpasang menggunakan PackageManager resmi.
 */
class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val selfPackageName: String = context.packageName

    /**
     * Memindai seluruh aplikasi yang memiliki Activity peluncur (Launcher Intent).
     */
    suspend fun getInstalledLaunchableApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(0L)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(launcherIntent, 0)
            }
        } catch (e: Exception) {
            emptyList()
        }

        val appList = mutableListOf<AppInfo>()
        val seenPackages = mutableSetOf<String>()

        for (resolveInfo in resolveInfos) {
            val activityInfo = resolveInfo.activityInfo ?: continue
            val packageName = activityInfo.packageName ?: continue

            // Jangan masukkan ZX SplitPlay sendiri ke daftar
            if (packageName == selfPackageName || seenPackages.contains(packageName)) {
                continue
            }
            seenPackages.add(packageName)

            val label = try {
                resolveInfo.loadLabel(packageManager).toString()
            } catch (e: Exception) {
                packageName
            }

            val icon = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }

            val isGame = isGameApp(packageName, activityInfo.applicationInfo)

            val versionName = try {
                val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0)
                }
                pkgInfo.versionName ?: ""
            } catch (e: Exception) {
                ""
            }

            appList.add(
                AppInfo(
                    packageName = packageName,
                    label = label,
                    isGame = isGame,
                    isInstalled = true,
                    versionName = versionName,
                    icon = icon
                )
            )
        }

        // Urutkan berdasarkan label aplikasi
        appList.sortedBy { it.label.lowercase() }
    }

    /**
     * Memeriksa apakah suatu paket adalah game melalui tanda kategori sistem atau nama paket.
     */
    private fun isGameApp(packageName: String, appInfo: ApplicationInfo?): Boolean {
        if (appInfo != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (appInfo.category == ApplicationInfo.CATEGORY_GAME) {
                    return true
                }
            }
            @Suppress("DEPRECATION")
            if ((appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0) {
                return true
            }
        }

        val lowerPkg = packageName.lowercase()
        val gameKeywords = listOf("game", "unity", "roblox", "pubg", "genshin", "mlbb", "freefire", "tencent", "epicgames")
        return gameKeywords.any { lowerPkg.contains(it) }
    }

    /**
     * Memeriksa apakah aplikasi tertentu masih terpasang dan dapat diluncurkan.
     */
    fun isAppAvailable(packageName: String): Boolean {
        if (packageName.isBlank()) return false
        return try {
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            appInfo.enabled && packageManager.getLaunchIntentForPackage(packageName) != null
        } catch (e: PackageManager.NameNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Mengambil AppInfo tunggal berdasarkan packageName.
     */
    fun getAppInfo(packageName: String): AppInfo? {
        if (packageName.isBlank()) return null
        return try {
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            val label = packageManager.getApplicationLabel(appInfo).toString()
            val icon = packageManager.getApplicationIcon(appInfo)
            val isGame = isGameApp(packageName, appInfo)
            AppInfo(
                packageName = packageName,
                label = label,
                isGame = isGame,
                isInstalled = appInfo.enabled,
                icon = icon
            )
        } catch (e: Exception) {
            null
        }
    }
}
