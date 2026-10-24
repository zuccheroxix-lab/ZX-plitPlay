package com.example.ui.screens

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.components.AppHeader
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyOutline
import com.example.ui.theme.BurgundySurfaceVariant
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DiagnosticsScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics

    val memInfo = remember {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val info = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(info)
        info
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BurgundyBackground)
    ) {
        AppHeader(
            title = "Diagnostik Perangkat & Sistem",
            canNavigateBack = true,
            onNavigateBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bagian Perangkat Keras
            item {
                DiagnosticSection(title = "Perangkat Keras & Manufaktur") {
                    DiagnosticRow("Manufaktur", Build.MANUFACTURER.uppercase())
                    DiagnosticRow("Model", Build.MODEL)
                    DiagnosticRow("Merek (Brand)", Build.BRAND)
                    DiagnosticRow("Board / Hardware", "${Build.BOARD} / ${Build.HARDWARE}")
                    DiagnosticRow("Arsitektur ABI", Build.SUPPORTED_ABIS.joinToString(", "))
                }
            }

            // Bagian Sistem Operasi Android
            item {
                DiagnosticSection(title = "Sistem Operasi Android") {
                    DiagnosticRow("Versi Android", "Android ${Build.VERSION.RELEASE}")
                    DiagnosticRow("API Level (SDK)", "${Build.VERSION.SDK_INT}")
                    DiagnosticRow("Build ID", Build.DISPLAY)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        DiagnosticRow("Patch Keamanan", Build.VERSION.SECURITY_PATCH)
                    }
                    DiagnosticRow("Multi-Window Support", if (uiState.deviceReport?.isMultiWindowSupported == true) "Aktif (AOSP)" else "Dibatasi Sistem")
                }
            }

            // Bagian Memori (RAM)
            item {
                val totalMb = memInfo.totalMem / (1024 * 1024)
                val availMb = memInfo.availMem / (1024 * 1024)
                val thresholdMb = memInfo.threshold / (1024 * 1024)

                DiagnosticSection(title = "Memori RAM & Alokasi") {
                    DiagnosticRow("Total RAM Fisik", "$totalMb MB (${totalMb / 1024} GB)")
                    DiagnosticRow("RAM Tersedia", "$availMb MB")
                    DiagnosticRow("Batas Memori Rendah", "$thresholdMb MB")
                    DiagnosticRow("Status Low Memory", if (memInfo.lowMemory) "Ya (Kritis)" else "Normal")
                    DiagnosticRow("JVM Max Heap", "${Runtime.getRuntime().maxMemory() / (1024 * 1024)} MB")
                }
            }

            // Bagian Tampilan & Layar
            item {
                DiagnosticSection(title = "Dimensi Layar & Tampilan") {
                    DiagnosticRow("Resolusi Layar", "${displayMetrics.widthPixels} x ${displayMetrics.heightPixels} px")
                    DiagnosticRow("Kepadatan (Density DPI)", "${displayMetrics.densityDpi} dpi (${displayMetrics.density}x)")
                    DiagnosticRow("Picture-in-Picture", if (uiState.deviceReport?.isPictureInPictureSupported == true) "Didukung" else "Tidak Aktif")
                    DiagnosticRow("Freeform Windowing", if (uiState.deviceReport?.isFreeformSupported == true) "Didukung" else "Memerlukan Opsi Pengembang")
                }
            }
        }
    }
}

@Composable
private fun DiagnosticSection(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        color = BurgundySurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = CrimsonPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMuted, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
