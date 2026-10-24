package com.example.overlay

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.MainActivity
import com.example.R
import com.example.data.SettingsRepository
import com.example.launcher.SplitScreenLauncher
import kotlin.math.abs

/**
 * Service pengelola panel kontrol mengambang ZX SplitPlay menggunakan WindowManager.
 */
class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var splitLauncher: SplitScreenLauncher

    private var bubbleView: View? = null
    private var panelView: View? = null

    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var panelParams: WindowManager.LayoutParams

    private var isPanelExpanded = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        settingsRepository = SettingsRepository(this)
        splitLauncher = SplitScreenLauncher(this)

        startForegroundNotification()

        if (checkOverlayPermission()) {
            setupViews()
        } else {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun startForegroundNotification() {
        val channelId = "zx_splitplay_floating"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "ZX SplitPlay Floating Controller",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifikasi status panel kontrol mengambang"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(channel)
        }

        val openIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("ZX SplitPlay Aktif")
                .setContentText("Panel kontrol mengambang siap digunakan.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("ZX SplitPlay Aktif")
                .setContentText("Panel kontrol mengambang siap digunakan.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build()
        }

        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            // Ignored if foreground limit on target OS
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        val currentSettings = settingsRepository.settings.value
        val buttonSizePx = dpToPx(currentSettings.floatingSizeDp)
        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // 1. Inisialisasi Tombol Mengambang (Bubble)
        val bubbleFrame = FrameLayout(this).apply {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#E6294D"))
                setStroke(dpToPx(2), Color.parseColor("#FFFFFF"))
            }
            background = bg
            alpha = currentSettings.floatingOpacity
            elevation = 16f
        }

        val bubbleIcon = ImageView(this).apply {
            setImageResource(R.mipmap.ic_launcher)
            setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
        }
        bubbleFrame.addView(bubbleIcon)

        bubbleParams = WindowManager.LayoutParams(
            buttonSizePx,
            buttonSizePx,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 200
        }

        // Gesture Drag & Click pada Bubble
        bubbleFrame.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isDragging = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = bubbleParams.x
                        initialY = bubbleParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isDragging = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()
                        if (abs(dx) > 10 || abs(dy) > 10) {
                            isDragging = true
                        }
                        if (isDragging) {
                            bubbleParams.x = initialX + dx
                            bubbleParams.y = initialY + dy
                            try {
                                windowManager.updateViewLayout(bubbleFrame, bubbleParams)
                            } catch (e: Exception) {
                                // View might be detached
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            triggerHaptic()
                            togglePanel()
                        } else {
                            // Snap ke tepi layar kiri atau kanan
                            snapToEdge()
                        }
                        return true
                    }
                }
                return false
            }
        })

        bubbleView = bubbleFrame
        try {
            windowManager.addView(bubbleFrame, bubbleParams)
        } catch (e: Exception) {
            stopSelf()
            return
        }

        // 2. Inisialisasi Panel Kontrol Lengkap
        setupPanelView(windowType)
    }

    private fun snapToEdge() {
        val bubble = bubbleView ?: return
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val middle = screenWidth / 2
        bubbleParams.x = if (bubbleParams.x < middle) 16 else screenWidth - bubbleParams.width - 16
        try {
            windowManager.updateViewLayout(bubble, bubbleParams)
        } catch (e: Exception) {
            // Ignored
        }
    }

    private fun setupPanelView(windowType: Int) {
        val settings = settingsRepository.settings.value

        val panelContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dpToPx(16).toFloat()
                setColor(Color.parseColor("#1C060D"))
                setStroke(dpToPx(1), Color.parseColor("#4E1626"))
            }
            background = bg
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            alpha = settings.floatingOpacity
        }

        val scroll = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val innerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Header Panel
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, dpToPx(12))
        }

        val titleView = TextView(this).apply {
            text = "ZX SplitPlay"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val closeBtn = TextView(this).apply {
            text = "✕"
            textSize = 16f
            setTextColor(Color.parseColor("#D6B2BC"))
            setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4))
            setOnClickListener {
                triggerHaptic()
                togglePanel()
            }
        }

        headerLayout.addView(titleView)
        headerLayout.addView(closeBtn)
        innerLayout.addView(headerLayout)

        // Tombol 1: Buka Game
        val gameBtn = createStyledButton(
            text = if (settings.selectedGameLabel.isNotBlank()) "🎮 ${settings.selectedGameLabel}" else "🎮 Buka Game Utama",
            bgColor = "#8F152D"
        ) {
            triggerHaptic()
            if (settings.selectedGamePackage.isNotBlank()) {
                splitLauncher.launchSingleApp(settings.selectedGamePackage)
            } else {
                returnToMainApp()
            }
        }
        innerLayout.addView(gameBtn)

        // Tombol 2: Buka Aplikasi Kedua
        val secondBtn = createStyledButton(
            text = if (settings.selectedSecondAppLabel.isNotBlank()) "📱 ${settings.selectedSecondAppLabel}" else "📱 Buka Aplikasi Kedua",
            bgColor = "#38101C"
        ) {
            triggerHaptic()
            if (settings.selectedSecondAppPackage.isNotBlank()) {
                splitLauncher.launchSingleApp(settings.selectedSecondAppPackage)
            } else {
                returnToMainApp()
            }
        }
        innerLayout.addView(secondBtn)

        // Tombol 3: Coba Split-Screen
        val splitBtn = createStyledButton(
            text = "⚡ Jalankan Alur Split",
            bgColor = "#E6294D"
        ) {
            triggerHaptic()
            splitLauncher.launchSplitPair(
                gamePackage = settings.selectedGamePackage,
                secondAppPackage = settings.selectedSecondAppPackage
            ) { /* Handled */ }
        }
        innerLayout.addView(splitBtn)

        // Tombol 4: Kembali ke Dashboard ZX SplitPlay
        val homeBtn = createStyledButton(
            text = "🏠 Kembali ke ZX SplitPlay",
            bgColor = "#240A12"
        ) {
            triggerHaptic()
            returnToMainApp()
        }
        innerLayout.addView(homeBtn)

        // Tombol 5: Tutup Floating Panel
        val exitBtn = createStyledButton(
            text = "⏹ Matikan Panel Mengambang",
            bgColor = "#17060B"
        ) {
            triggerHaptic()
            stopSelf()
        }
        innerLayout.addView(exitBtn)

        // Catatan Edukatif Pengguna (Jujur)
        val noteView = TextView(this).apply {
            text = "Catatan: Panel ini adalah asisten pengendali ZX SplitPlay. Jika ponsel Anda tidak membagi layar otomatis, gunakan tombol aplikasi di atas bersama fitur Recents sistem."
            textSize = 10f
            setTextColor(Color.parseColor("#9E7C86"))
            setPadding(0, dpToPx(8), 0, 0)
        }
        innerLayout.addView(noteView)

        scroll.addView(innerLayout)
        panelContainer.addView(scroll)

        val panelWidth = dpToPx(280)
        panelParams = WindowManager.LayoutParams(
            panelWidth,
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        panelView = panelContainer
    }

    private fun createStyledButton(text: String, bgColor: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 12f
            isAllCaps = false
            val shape = GradientDrawable().apply {
                cornerRadius = dpToPx(8).toFloat()
                setColor(Color.parseColor(bgColor))
                setStroke(dpToPx(1), Color.parseColor("#4E1626"))
            }
            background = shape
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(44)
            ).apply {
                setMargins(0, 0, 0, dpToPx(6))
            }
            layoutParams = params
            setOnClickListener { onClick() }
        }
    }

    private fun togglePanel() {
        val panel = panelView ?: return
        if (isPanelExpanded) {
            try {
                windowManager.removeView(panel)
            } catch (e: Exception) {
                // View wasn't attached
            }
            isPanelExpanded = false
        } else {
            try {
                windowManager.addView(panel, panelParams)
                isPanelExpanded = true
            } catch (e: Exception) {
                // Cannot attach
            }
        }
    }

    private fun returnToMainApp() {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        try {
            startActivity(mainIntent)
        } catch (e: Exception) {
            // Ignored
        }
        if (isPanelExpanded) {
            togglePanel()
        }
    }

    private fun triggerHaptic() {
        if (!settingsRepository.settings.value.hapticFeedbackEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(30)
                }
            }
        } catch (e: Exception) {
            // Ignored if permission missing or device has no vibrator
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    override fun onDestroy() {
        isRunning = false
        bubbleView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                // Ignored
            }
        }
        panelView?.let {
            if (isPanelExpanded) {
                try {
                    windowManager.removeView(it)
                } catch (e: Exception) {
                    // Ignored
                }
            }
        }
        bubbleView = null
        panelView = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.zucchero.zxsplitplay.ACTION_START_FLOATING"
        const val ACTION_STOP = "com.zucchero.zxsplitplay.ACTION_STOP_FLOATING"
        private const val NOTIFICATION_ID = 9182

        @Volatile
        var isRunning: Boolean = false
            private set
    }
}
