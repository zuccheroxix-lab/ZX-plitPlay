package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SupportLevel
import com.example.ui.theme.BurgundyOutline
import com.example.ui.theme.BurgundySurface
import com.example.ui.theme.BurgundySurfaceVariant
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Konversi Android Drawable menjadi Bitmap yang aman dirender di Compose.
 */
fun Drawable.toBitmapSafe(): Bitmap? {
    return try {
        if (this is BitmapDrawable && this.bitmap != null) {
            return this.bitmap
        }
        val w = if (intrinsicWidth > 0) intrinsicWidth else 96
        val h = if (intrinsicHeight > 0) intrinsicHeight else 96
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AppIconView(
    drawable: Drawable?,
    modifier: Modifier = Modifier,
    fallbackText: String = ""
) {
    val bitmap = remember(drawable) { drawable?.toBitmapSafe() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BurgundySurfaceVariant)
            .border(1.dp, BurgundyOutline, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = fallbackText.ifEmpty { "Ikon Aplikasi" },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        } else {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "Aplikasi",
                tint = CrimsonPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun StatusBadge(
    level: SupportLevel,
    modifier: Modifier = Modifier
) {
    val (bgColor, icon, label) = when (level) {
        SupportLevel.SUPPORTED -> Triple(StatusSuccess, Icons.Default.CheckCircle, "Didukung")
        SupportLevel.SUPPORTED_WITH_LIMITS -> Triple(StatusWarning, Icons.Default.Warning, "Didukung (Batasan)")
        SupportLevel.PERMISSION_REQUIRED -> Triple(StatusInfo, Icons.Default.Info, "Perlu Izin")
        SupportLevel.NOT_SUPPORTED -> Triple(StatusError, Icons.Default.Error, "Tidak Didukung")
        SupportLevel.UNVERIFIED -> Triple(TextMuted, Icons.Default.HelpOutline, "Belum Diverifikasi")
    }

    Surface(
        color = bgColor.copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, bgColor.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = bgColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = bgColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GamingCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String = "",
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BurgundySurfaceVariant)
            .border(1.dp, BurgundyOutline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(16.dp)
    } else {
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BurgundySurfaceVariant)
            .border(1.dp, BurgundyOutline, RoundedCornerShape(16.dp))
            .testTag(testTag)
            .padding(16.dp)
    }

    Box(modifier = cardModifier) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    title: String,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("btn_back")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = TextPrimary
                    )
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BurgundySurface,
            titleContentColor = TextPrimary
        )
    )
}
