package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.launcher.LaunchResult
import com.example.model.SupportLevel
import com.example.ui.AppScreen
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.TargetAppType
import com.example.ui.components.AppIconView
import com.example.ui.components.GamingCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyOutline
import com.example.ui.theme.BurgundySurfaceContainer
import com.example.ui.theme.BurgundySurfaceVariant
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.CrimsonSecondary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val report = uiState.deviceReport
    val settings = uiState.userSettings

    // Cari app icon terpasang
    val gameApp = remember(uiState.allApps, settings.selectedGamePackage) {
        uiState.allApps.find { it.packageName == settings.selectedGamePackage }
    }
    val secondApp = remember(uiState.allApps, settings.selectedSecondAppPackage) {
        uiState.allApps.find { it.packageName == settings.selectedSecondAppPackage }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BurgundyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Status Kompatibilitas Perangkat Nyata
        item {
            GamingCard(
                onClick = { viewModel.navigateTo(AppScreen.COMPATIBILITY) },
                testTag = "card_compatibility_summary"
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonSecondary.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = CrimsonPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Status Perangkat",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (report != null) "${report.manufacturer.uppercase()} ${report.model} • Android ${report.androidRelease}" else "Memeriksa perangkat...",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (report != null) {
                            val level = if (report.isMultiWindowSupported) SupportLevel.SUPPORTED else SupportLevel.NOT_SUPPORTED
                            StatusBadge(level = level)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (report?.isMultiWindowSupported == true) "Multi-window Android resmi didukung" else "Perangkat membatasi multi-window",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Detail",
                                color = CrimsonPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Kartu Pemilihan Pasangan Aplikasi
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Pasangan Layar Pisah",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // Kartu Game Utama
                GamingCard(
                    onClick = { viewModel.navigateTo(AppScreen.APP_SELECTOR, TargetAppType.GAME) },
                    testTag = "card_select_game"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconView(
                            drawable = gameApp?.icon,
                            modifier = Modifier.size(48.dp),
                            fallbackText = settings.selectedGameLabel
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Game Utama",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = settings.selectedGameLabel.ifEmpty { "Pilih Game Utama" },
                                color = if (settings.selectedGameLabel.isNotEmpty()) TextPrimary else CrimsonPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (settings.selectedGamePackage.isNotEmpty()) {
                                Text(
                                    text = settings.selectedGamePackage,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = "Pilih Game",
                            tint = if (settings.selectedGamePackage.isNotEmpty()) StatusSuccess else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Kartu Aplikasi Kedua
                GamingCard(
                    onClick = { viewModel.navigateTo(AppScreen.APP_SELECTOR, TargetAppType.SECOND_APP) },
                    testTag = "card_select_second_app"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconView(
                            drawable = secondApp?.icon,
                            modifier = Modifier.size(48.dp),
                            fallbackText = settings.selectedSecondAppLabel
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aplikasi Kedua (Browser, Chat, Panduan)",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = settings.selectedSecondAppLabel.ifEmpty { "Pilih Aplikasi Kedua" },
                                color = if (settings.selectedSecondAppLabel.isNotEmpty()) TextPrimary else CrimsonPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (settings.selectedSecondAppPackage.isNotEmpty()) {
                                Text(
                                    text = settings.selectedSecondAppPackage,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = "Pilih Aplikasi Kedua",
                            tint = if (settings.selectedSecondAppPackage.isNotEmpty()) StatusSuccess else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Tombol Aksi Utama
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.launchSplitScreen() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_launch_split_screen"),
                    enabled = !uiState.isLaunching && settings.selectedGamePackage.isNotEmpty() && settings.selectedSecondAppPackage.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonPrimary,
                        disabledContainerColor = BurgundySurfaceContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLaunching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Menjalankan Alur Split...", color = Color.White)
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Luncurkan Split-Screen",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Tombol Floating Panel
                OutlinedButton(
                    onClick = {
                        if (uiState.isFloatingServiceRunning) {
                            viewModel.stopFloatingService()
                        } else {
                            viewModel.startFloatingService()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_toggle_floating_service"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyOutline),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isFloatingServiceRunning) Icons.Default.Stop else Icons.Default.Layers,
                        contentDescription = null,
                        tint = if (uiState.isFloatingServiceRunning) StatusError else AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isFloatingServiceRunning) "Matikan Panel Mengambang" else "Buka Floating Control Panel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Tampilan Hasil Peluncuran
        uiState.lastLaunchResult?.let { result ->
            item {
                when (result) {
                    is LaunchResult.Dispatched -> {
                        Surface(
                            color = BurgundySurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("result_dispatched")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = StatusSuccess,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Alur Peluncuran Dikirim",
                                        color = StatusSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = result.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Butuh panduan manual sesuai merek HP? Buka Asisten Layar Pisah.",
                                    color = CrimsonPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable {
                                        viewModel.navigateTo(AppScreen.SPLIT_ASSISTANT)
                                    }
                                )
                            }
                        }
                    }
                    is LaunchResult.Error -> {
                        Surface(
                            color = BurgundySurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StatusError.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("result_error")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = StatusError,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Gagal Membuka Aplikasi",
                                        color = StatusError,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = result.reason,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigasi Fitur Pendukung
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Menu Utilitas",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                // Shortcut Assistant
                MenuRowItem(
                    icon = Icons.Default.Build,
                    title = "Asisten Layar Pisah (Panduan OEM)",
                    subtitle = "Langkah manual Xiaomi, Samsung, Oppo, Vivo, dan AOSP",
                    onClick = { viewModel.navigateTo(AppScreen.SPLIT_ASSISTANT) },
                    testTag = "menu_split_assistant"
                )

                // Shortcut Floating Settings
                MenuRowItem(
                    icon = Icons.Default.Layers,
                    title = "Pengaturan Floating Panel",
                    subtitle = "Ukuran tombol, transparansi, haptic feedback",
                    onClick = { viewModel.navigateTo(AppScreen.FLOATING_SETTINGS) },
                    testTag = "menu_floating_settings"
                )

                // Shortcut Diagnostics
                MenuRowItem(
                    icon = Icons.Default.Speed,
                    title = "Diagnostik Perangkat & RAM",
                    subtitle = "Detail spesifikasi OS, API level, dan alokasi memori",
                    onClick = { viewModel.navigateTo(AppScreen.DIAGNOSTICS) },
                    testTag = "menu_diagnostics"
                )

                // Shortcut Permissions
                MenuRowItem(
                    icon = Icons.Default.Security,
                    title = "Izin & Visibilitas Sistem",
                    subtitle = "Status SYSTEM_ALERT_WINDOW dan deklarasi query",
                    onClick = { viewModel.navigateTo(AppScreen.PERMISSIONS) },
                    testTag = "menu_permissions"
                )

                // Shortcut About
                MenuRowItem(
                    icon = Icons.Default.Info,
                    title = "Tentang ZX SplitPlay",
                    subtitle = "Pengembang ZUCCHERO XANN, build, dan batasan jujur",
                    onClick = { viewModel.navigateTo(AppScreen.ABOUT) },
                    testTag = "menu_about"
                )
            }
        }
    }
}

@Composable
private fun MenuRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = BurgundySurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CrimsonPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
