package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.TargetAppType
import com.example.ui.components.AppHeader
import com.example.ui.components.AppIconView
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyOutline
import com.example.ui.theme.BurgundySurface
import com.example.ui.theme.BurgundySurfaceVariant
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppSelectorScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val target = uiState.appSelectorTarget
    val title = if (target == TargetAppType.GAME) "Pilih Game Utama" else "Pilih Aplikasi Kedua"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BurgundyBackground)
    ) {
        AppHeader(
            title = title,
            canNavigateBack = true,
            onNavigateBack = { viewModel.navigateBack() },
            actions = {
                IconButton(
                    onClick = { viewModel.loadInstalledApps() },
                    modifier = Modifier.testTag("btn_refresh_apps")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Muat Ulang Aplikasi",
                        tint = CrimsonPrimary
                    )
                }
            }
        )

        // Target Selector Tabs
        TabRow(
            selectedTabIndex = if (target == TargetAppType.GAME) 0 else 1,
            containerColor = BurgundySurface,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (target == TargetAppType.GAME) 0 else 1]
                    ),
                    color = CrimsonPrimary
                )
            }
        ) {
            Tab(
                selected = target == TargetAppType.GAME,
                onClick = { viewModel.setAppSelectorTarget(TargetAppType.GAME) },
                text = {
                    Text(
                        text = "Game Utama",
                        fontWeight = if (target == TargetAppType.GAME) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_select_game")
            )
            Tab(
                selected = target == TargetAppType.SECOND_APP,
                onClick = { viewModel.setAppSelectorTarget(TargetAppType.SECOND_APP) },
                text = {
                    Text(
                        text = "Aplikasi Kedua",
                        fontWeight = if (target == TargetAppType.SECOND_APP) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.testTag("tab_select_second_app")
            )
        }

        // Kolom Pencarian
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("input_search_apps"),
            placeholder = { Text("Cari nama atau paket aplikasi...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextMuted
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Hapus",
                            tint = TextMuted
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BurgundySurfaceVariant,
                unfocusedContainerColor = BurgundySurfaceVariant,
                focusedBorderColor = CrimsonPrimary,
                unfocusedBorderColor = BurgundyOutline,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Status Jumlah & Keterangan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.filteredApps.size} aplikasi terpasang ditemukan",
                color = TextMuted,
                fontSize = 11.sp
            )
            if (target == TargetAppType.GAME) {
                Text(
                    text = "🎮 Game diprioritaskan di atas",
                    color = AccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Tampilan Daftar Aplikasi
        if (uiState.isLoadingApps) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = CrimsonPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Memindai aplikasi melalui PackageManager...",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else if (uiState.filteredApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.searchQuery.isNotEmpty()) "Tidak ada aplikasi yang cocok dengan '${uiState.searchQuery}'" else "Tidak ada aplikasi yang ditemukan.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredApps, key = { it.packageName }) { app ->
                    AppRowItem(
                        app = app,
                        onClick = { viewModel.selectApp(app, target) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRowItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    Surface(
        color = BurgundySurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("app_item_${app.packageName}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconView(
                drawable = app.icon,
                modifier = Modifier.size(44.dp),
                fallbackText = app.label
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.label,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (app.isGame) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = CrimsonPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "GAME",
                                color = CrimsonPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = app.packageName,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
