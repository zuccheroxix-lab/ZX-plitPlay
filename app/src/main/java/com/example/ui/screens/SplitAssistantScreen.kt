package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainUiState
import com.example.ui.MainViewModel
import com.example.ui.components.AppHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyOutline
import com.example.ui.theme.BurgundySurfaceVariant
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SplitAssistantScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BurgundyBackground)
    ) {
        AppHeader(
            title = "Asisten Layar Pisah",
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
            // Informasi Fakta Teknis Android
            item {
                Surface(
                    color = BurgundySurfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Cara Kerja Split-Screen di Android",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Android menyediakan bendera sistem FLAG_ACTIVITY_LAUNCH_ADJACENT untuk meminta peluncuran berdampingan. Namun, produsen perangkat (OEM) memegang kendali penuh apakah pemisahan dilakukan otomatis atau melalui antarmuka aplikasi terbaru (Recents).",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Panduan Opsi Pengembang (Game tidak mendukung multi-window)
            item {
                Surface(
                    color = BurgundySurfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tips: Game Menolak Layar Pisah?",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sebagian pengembang game menonaktifkan atribut 'resizeableActivity' di manifes mereka. Anda dapat mengatasi ini secara resmi:\n1. Buka Pengaturan HP > Tentang Ponsel.\n2. Ketuk 'Nomor Versi / Build Number' 7 kali hingga Opsi Pengembang aktif.\n3. Masuk ke Opsi Pengembang > cari dan aktifkan 'Paksa aktivitas dapat diubah ukuran' (Force activities to be resizable).\n4. Restart HP Anda.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Panduan Langkah Manual per Merek OEM
            item {
                Text(
                    text = "Panduan Manual Berdasarkan Merek HP",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            item {
                OemGuideCard(
                    brand = "Samsung Galaxy (One UI)",
                    steps = listOf(
                        "Buka game yang diinginkan terlebih dahulu.",
                        "Buka layar Aplikasi Terbaru (Recents / tombol garis tiga atau geser dari bawah).",
                        "Ketuk IKON GAME di bagian atas kartu pratinjau.",
                        "Pilih menu 'Buka dalam tampilan layar pisah' (Open in split screen view).",
                        "Pilih aplikasi kedua dari daftar aplikasi di layar bawah."
                    )
                )
            }

            item {
                OemGuideCard(
                    brand = "Xiaomi / Redmi / POCO (MIUI & HyperOS)",
                    steps = listOf(
                        "Buka game utama.",
                        "Buka layar Recent Apps.",
                        "Tahan (tekan lama) pada kartu game sampai muncul ikon pop-up samping, lalu ketuk ikon dua kotak (Split Screen).",
                        "Jika ikon tidak muncul di MIUI terbaru, gunakan fitur 'Jendela Mengambang' (Floating Window) dari Game Turbo atau aktifkan Opsi Pengembang di atas.",
                        "Ketuk aplikasi kedua untuk mengisi paruh layar lainnya."
                    )
                )
            }

            item {
                OemGuideCard(
                    brand = "OPPO / Realme / OnePlus (ColorOS)",
                    steps = listOf(
                        "Masuk ke game yang ingin dimainkan.",
                        "Geser ke atas pada layar menggunakan 3 jari secara bersamaan.",
                        "Layar game akan otomatis mengecil ke bagian atas.",
                        "Pilih aplikasi kedua di bagian bawah layar.",
                        "Atur pembatas layar sesuai kebutuhan kenyamanan Anda."
                    )
                )
            }

            item {
                OemGuideCard(
                    brand = "Vivo / iQOO (FuntouchOS)",
                    steps = listOf(
                        "Buka game.",
                        "Geser ke bawah layar dengan 3 jari untuk memunculkan Smart Split.",
                        "Atau buka Recents, ketuk panah/ikon di samping judul kartu aplikasi, pilih 'Layar Terpisah'.",
                        "Pilih aplikasi pendamping kedua."
                    )
                )
            }

            item {
                OemGuideCard(
                    brand = "Google Pixel / Motorola / Stock Android",
                    steps = listOf(
                        "Buka game yang hendak dimainkan.",
                        "Buka navigasi Recents (geser dari bawah layar dan tahan sebentar).",
                        "Ketuk ikon lingkaran aplikasi di atas jendela pratinjau game.",
                        "Pilih menu 'Layar Pisah' (Split Top / Split Screen).",
                        "Ketuk kartu aplikasi kedua dari daftar yang tersedia."
                    )
                )
            }

            // Tombol coba peluncuran langsung
            item {
                Button(
                    onClick = { viewModel.launchSplitScreen() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_assistant_launch_split"),
                    enabled = uiState.userSettings.selectedGamePackage.isNotEmpty() && uiState.userSettings.selectedSecondAppPackage.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coba Alur Split Sekarang", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun OemGuideCard(
    brand: String,
    steps: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = BurgundySurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BurgundyOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
            .testTag("oem_card_${brand.take(6)}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = brand,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}. ",
                                color = CrimsonPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = step,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
