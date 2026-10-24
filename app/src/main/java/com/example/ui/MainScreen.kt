package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AppSelectorScreen
import com.example.ui.screens.CompatibilityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.FloatingSettingsScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SplitAssistantScreen
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyOutline
import com.example.ui.theme.BurgundySurface
import com.example.ui.theme.BurgundySurfaceContainer
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BurgundyBackground),
        topBar = {
            if (uiState.currentScreen == AppScreen.DASHBOARD) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.mipmap.ic_launcher),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "ZX SplitPlay",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.ABOUT) },
                            modifier = Modifier.testTag("btn_top_about")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Tentang Aplikasi",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BurgundySurface,
                        titleContentColor = TextPrimary
                    )
                )
            }
        },
        bottomBar = {
            // Tampilkan navigasi bawah hanya pada layar utama
            if (uiState.currentScreen in listOf(
                    AppScreen.DASHBOARD,
                    AppScreen.COMPATIBILITY,
                    AppScreen.SPLIT_ASSISTANT,
                    AppScreen.FLOATING_SETTINGS
                )
            ) {
                NavigationBar(
                    containerColor = BurgundySurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = BurgundyOutline
                    )
                ) {
                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonPrimary,
                            selectedTextColor = CrimsonPrimary,
                            indicatorColor = BurgundySurfaceContainer,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_dashboard")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.COMPATIBILITY,
                        onClick = { viewModel.navigateTo(AppScreen.COMPATIBILITY) },
                        icon = { Icon(Icons.Default.Speed, contentDescription = "Kompatibilitas") },
                        label = { Text("Kompatibilitas", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonPrimary,
                            selectedTextColor = CrimsonPrimary,
                            indicatorColor = BurgundySurfaceContainer,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_compatibility")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.SPLIT_ASSISTANT,
                        onClick = { viewModel.navigateTo(AppScreen.SPLIT_ASSISTANT) },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Asisten") },
                        label = { Text("Asisten", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonPrimary,
                            selectedTextColor = CrimsonPrimary,
                            indicatorColor = BurgundySurfaceContainer,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_assistant")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == AppScreen.FLOATING_SETTINGS,
                        onClick = { viewModel.navigateTo(AppScreen.FLOATING_SETTINGS) },
                        icon = { Icon(Icons.Default.Layers, contentDescription = "Floating") },
                        label = { Text("Floating", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonPrimary,
                            selectedTextColor = CrimsonPrimary,
                            indicatorColor = BurgundySurfaceContainer,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_floating")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BurgundyBackground)
        ) {
            Crossfade(targetState = uiState.currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    AppScreen.DASHBOARD -> DashboardScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.COMPATIBILITY -> CompatibilityScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.APP_SELECTOR -> AppSelectorScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.SPLIT_ASSISTANT -> SplitAssistantScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.FLOATING_SETTINGS -> FloatingSettingsScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.DIAGNOSTICS -> DiagnosticsScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.PERMISSIONS -> PermissionsScreen(uiState = uiState, viewModel = viewModel)
                    AppScreen.ABOUT -> AboutScreen(uiState = uiState, viewModel = viewModel)
                }
            }
        }
    }
}
