package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.compatibility.DeviceCompatibilityChecker
import com.example.data.AppRepository
import com.example.data.SettingsRepository
import com.example.launcher.LaunchResult
import com.example.launcher.SplitScreenLauncher
import com.example.model.AppInfo
import com.example.model.DeviceReport
import com.example.model.UserSettings
import com.example.overlay.FloatingControlService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    COMPATIBILITY,
    APP_SELECTOR,
    SPLIT_ASSISTANT,
    FLOATING_SETTINGS,
    DIAGNOSTICS,
    PERMISSIONS,
    ABOUT
}

enum class TargetAppType {
    GAME,
    SECOND_APP
}

data class MainUiState(
    val currentScreen: AppScreen = AppScreen.DASHBOARD,
    val previousScreen: AppScreen = AppScreen.DASHBOARD,
    val appSelectorTarget: TargetAppType = TargetAppType.GAME,
    val deviceReport: DeviceReport? = null,
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val isLoadingApps: Boolean = false,
    val searchQuery: String = "",
    val userSettings: UserSettings = UserSettings(),
    val isFloatingServiceRunning: Boolean = false,
    val lastLaunchResult: LaunchResult? = null,
    val isLaunching: Boolean = false,
    val hasOverlayPermission: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    private val appRepository = AppRepository(context)
    private val settingsRepository = SettingsRepository(context)
    private val compatibilityChecker = DeviceCompatibilityChecker(context)
    private val splitLauncher = SplitScreenLauncher(context)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Pantau pengaturan tersimpan
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(userSettings = settings) }
            }
        }

        refreshCompatibility()
        loadInstalledApps()
        updateFloatingServiceStatus()
    }

    fun navigateTo(screen: AppScreen, target: TargetAppType? = null) {
        _uiState.update { current ->
            current.copy(
                previousScreen = current.currentScreen,
                currentScreen = screen,
                appSelectorTarget = target ?: current.appSelectorTarget
            )
        }
        if (screen == AppScreen.APP_SELECTOR) {
            filterApps("")
        }
    }

    fun navigateBack(): Boolean {
        val current = _uiState.value.currentScreen
        return if (current != AppScreen.DASHBOARD) {
            _uiState.update { it.copy(currentScreen = it.previousScreen) }
            true
        } else {
            false
        }
    }

    fun refreshCompatibility() {
        val report = compatibilityChecker.generateReport()
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
        _uiState.update {
            it.copy(
                deviceReport = report,
                hasOverlayPermission = hasOverlay
            )
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingApps = true) }
            val apps = appRepository.getInstalledLaunchableApps()
            _uiState.update {
                it.copy(
                    allApps = apps,
                    filteredApps = filterList(apps, it.searchQuery, it.appSelectorTarget),
                    isLoadingApps = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredApps = filterList(current.allApps, query, current.appSelectorTarget)
            )
        }
    }

    fun setAppSelectorTarget(target: TargetAppType) {
        _uiState.update { current ->
            current.copy(
                appSelectorTarget = target,
                filteredApps = filterList(current.allApps, current.searchQuery, target)
            )
        }
    }

    private fun filterApps(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredApps = filterList(current.allApps, query, current.appSelectorTarget)
            )
        }
    }

    private fun filterList(apps: List<AppInfo>, query: String, target: TargetAppType): List<AppInfo> {
        val trimmed = query.trim().lowercase()
        return apps.filter { app ->
            val matchesQuery = trimmed.isEmpty() ||
                    app.label.lowercase().contains(trimmed) ||
                    app.packageName.lowercase().contains(trimmed)

            // Jika memilih game, prioritaskan game
            val matchesCategory = if (target == TargetAppType.GAME && trimmed.isEmpty()) {
                // Tampilkan semua tapi game tetap di atas
                true
            } else {
                true
            }
            matchesQuery && matchesCategory
        }.sortedWith(
            if (target == TargetAppType.GAME) {
                compareByDescending<AppInfo> { it.isGame }.thenBy { it.label.lowercase() }
            } else {
                compareBy { it.label.lowercase() }
            }
        )
    }

    fun selectApp(app: AppInfo, target: TargetAppType) {
        if (target == TargetAppType.GAME) {
            settingsRepository.setSelectedGame(app.packageName, app.label)
        } else {
            settingsRepository.setSelectedSecondApp(app.packageName, app.label)
        }
        navigateBack()
    }

    fun launchSplitScreen() {
        val settings = _uiState.value.userSettings
        _uiState.update { it.copy(isLaunching = true, lastLaunchResult = null) }

        splitLauncher.launchSplitPair(
            gamePackage = settings.selectedGamePackage,
            secondAppPackage = settings.selectedSecondAppPackage
        ) { result ->
            _uiState.update { it.copy(isLaunching = false, lastLaunchResult = result) }

            // Jika diaktifkan, buka panel mengambang otomatis
            if (result is LaunchResult.Dispatched && settings.autoOpenFloatingOnSplit) {
                startFloatingService()
            }
        }
    }

    fun startFloatingService(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            navigateTo(AppScreen.PERMISSIONS)
            return false
        }

        val serviceIntent = Intent(context, FloatingControlService::class.java).apply {
            action = FloatingControlService.ACTION_START
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            _uiState.update { it.copy(isFloatingServiceRunning = true) }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun stopFloatingService() {
        val serviceIntent = Intent(context, FloatingControlService::class.java).apply {
            action = FloatingControlService.ACTION_STOP
        }
        try {
            context.startService(serviceIntent)
        } catch (e: Exception) {
            // Ignored
        }
        _uiState.update { it.copy(isFloatingServiceRunning = false) }
    }

    fun updateFloatingServiceStatus() {
        _uiState.update { it.copy(isFloatingServiceRunning = FloatingControlService.isRunning) }
    }

    fun setFloatingOpacity(opacity: Float) {
        settingsRepository.setFloatingOpacity(opacity)
    }

    fun setFloatingSize(sizeDp: Int) {
        settingsRepository.setFloatingSize(sizeDp)
    }

    fun setHapticFeedback(enabled: Boolean) {
        settingsRepository.setHapticFeedback(enabled)
    }

    fun setAutoOpenFloating(enabled: Boolean) {
        settingsRepository.setAutoOpenFloatingOnSplit(enabled)
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
        stopFloatingService()
    }
}
