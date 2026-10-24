package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Penyimpanan lokal preferensi pengguna menggunakan SharedPreferences Android.
 */
class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        return UserSettings(
            selectedGamePackage = prefs.getString(KEY_GAME_PKG, "") ?: "",
            selectedGameLabel = prefs.getString(KEY_GAME_LABEL, "") ?: "",
            selectedSecondAppPackage = prefs.getString(KEY_SECOND_APP_PKG, "") ?: "",
            selectedSecondAppLabel = prefs.getString(KEY_SECOND_APP_LABEL, "") ?: "",
            floatingOpacity = prefs.getFloat(KEY_FLOATING_OPACITY, 0.9f),
            floatingSizeDp = prefs.getInt(KEY_FLOATING_SIZE, 56),
            hapticFeedbackEnabled = prefs.getBoolean(KEY_HAPTIC, true),
            autoOpenFloatingOnSplit = prefs.getBoolean(KEY_AUTO_OPEN_FLOATING, true)
        )
    }

    fun setSelectedGame(packageName: String, label: String) {
        prefs.edit()
            .putString(KEY_GAME_PKG, packageName)
            .putString(KEY_GAME_LABEL, label)
            .apply()
        _settings.value = loadSettings()
    }

    fun setSelectedSecondApp(packageName: String, label: String) {
        prefs.edit()
            .putString(KEY_SECOND_APP_PKG, packageName)
            .putString(KEY_SECOND_APP_LABEL, label)
            .apply()
        _settings.value = loadSettings()
    }

    fun setFloatingOpacity(opacity: Float) {
        val clamped = opacity.coerceIn(0.3f, 1.0f)
        prefs.edit().putFloat(KEY_FLOATING_OPACITY, clamped).apply()
        _settings.value = loadSettings()
    }

    fun setFloatingSize(sizeDp: Int) {
        val validSize = when (sizeDp) {
            48, 64 -> sizeDp
            else -> 56
        }
        prefs.edit().putInt(KEY_FLOATING_SIZE, validSize).apply()
        _settings.value = loadSettings()
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, enabled).apply()
        _settings.value = loadSettings()
    }

    fun setAutoOpenFloatingOnSplit(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_OPEN_FLOATING, enabled).apply()
        _settings.value = loadSettings()
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settings.value = loadSettings()
    }

    companion object {
        private const val PREFS_NAME = "zx_splitplay_preferences"
        private const val KEY_GAME_PKG = "pref_game_package"
        private const val KEY_GAME_LABEL = "pref_game_label"
        private const val KEY_SECOND_APP_PKG = "pref_second_app_package"
        private const val KEY_SECOND_APP_LABEL = "pref_second_app_label"
        private const val KEY_FLOATING_OPACITY = "pref_floating_opacity"
        private const val KEY_FLOATING_SIZE = "pref_floating_size"
        private const val KEY_HAPTIC = "pref_haptic_feedback"
        private const val KEY_AUTO_OPEN_FLOATING = "pref_auto_open_floating"
    }
}
