# ZX SplitPlay 🎮📱

> **Gaming Utility & Native Split-Screen Assistant untuk Android**  
> Dikembangkan oleh **ZUCCHERO XANN**

---

## 📌 Ringkasan Proyek

**ZX SplitPlay** adalah aplikasi utilitas Android native yang dirancang khusus untuk mempermudah pemain game membuka dua aplikasi sekaligus secara berdampingan melalui fitur **Multi-Window / Split-Screen** resmi sistem Android, dilengkapi **Floating Control Panel** sebagai asisten kontrol cepat saat game sedang berjalan.

Aplikasi ini dibangun menggunakan arsitektur Android modern:
- **Bahasa:** 100% Kotlin
- **UI Toolkit:** Jetpack Compose + Material Design 3 (M3)
- **Tema Visual:** Dark Burgundy & Crimson Gaming Palette
- **Arsitektur:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Min SDK:** Android 7.0 (API 24 - AOSP Multi-Window)
- **Target SDK:** Android 14+ (API 34/36)

---

## 🚀 Fitur Utama

1. **Device Compatibility Checker (Deteksi Nyata):**
   - Memeriksa versi Android, API Level, dan dukungan Multi-Window sistem (`ActivityManager.supportsMultiWindow`).
   - Memeriksa izin overlay (`Settings.canDrawOverlays`).
   - Memeriksa kapabilitas hardware: RAM fisik, Freeform Window, dan Picture-in-Picture.
   - Tanpa klaim fiktif atau benchmark palsu.

2. **App Selector Terintegrasi:**
   - Memindai seluruh aplikasi terpasang di perangkat menggunakan `PackageManager` resmi.
   - Memisahkan dan mendeteksi kategori **Game** secara otomatis.
   - Dilengkapi kolom pencarian instan dan refresh berkala.
   - Mematuhi deklarasi `<queries>` resmi tanpa meminta izin berlebih.

3. **Native Split-Screen Launcher:**
   - Memanfaatkan bendera peluncuran resmi Android: `FLAG_ACTIVITY_LAUNCH_ADJACENT`, `FLAG_ACTIVITY_NEW_TASK`, dan `FLAG_ACTIVITY_MULTIPLE_TASK`.
   - Menangani kegagalan peluncuran, `ActivityNotFoundException`, dan `SecurityException`.
   - Menyediakan panduan manual khusus untuk Xiaomi (MIUI/HyperOS), Samsung (One UI), OPPO/Realme (ColorOS), Vivo (FuntouchOS), dan Google Pixel (Stock Android).

4. **Floating Control Panel (Panel Kontrol Mengambang):**
   - Dibangun menggunakan `WindowManager` dan `TYPE_APPLICATION_OVERLAY`.
   - Tombol mengambang (bubble) dengan gestur sentuh halus (drag & snap ke tepi layar).
   - Panel kontrol dapat diperluas untuk tombol cepat buka game, buka aplikasi kedua, alur split, dan tombol kembali ke ZX SplitPlay.
   - Pengaturan opasitas (transparansi 30%–100%), ukuran tombol (48dp, 56dp, 64dp), serta umpan balik getar (haptic feedback).
   - Pembersihan otomatis saat Service dihentikan (tidak meninggalkan sampah overlay di memori).

5. **Diagnostik Perangkat & RAM:**
   - Menampilkan informasi manufaktur, model, chipset, ABI, alokasi memori JVM, dan resolusi layar secara transparan.

---

## 📥 Panduan Unduh & Pemasangan APK

### A. Mengunduh Melalui GitHub Actions (Artifacts)
Setiap kali ada pembaruan kode di branch utama, GitHub Actions secara otomatis mengompilasi dan menguji APK.
1. Buka tab **Actions** pada repository GitHub.
2. Klik workflow run terbaru bertajuk **"ZX SplitPlay CI - Build APK"**.
3. Gulir ke bagian paling bawah ke bagian **Artifacts**.
4. Klik **`ZX-SplitPlay-debug-apk`** untuk mengunduh arsip ZIP yang berisi `ZX-SplitPlay-debug.apk`.
5. Ekstrak file APK di HP Android Anda.

### B. Mengunduh Melalui GitHub Releases
1. Buka halaman **Releases** di sisi kanan repository GitHub.
2. Temukan rilis dengan tag versi terbaru (misal: `v1.0.0`).
3. Pada bagian **Assets**, ketuk langsung file **`ZX-SplitPlay-debug.apk`** melalui browser HP Android Anda.

### C. Cara Memasang (Install) di HP Android
1. Buka file APK yang telah diunduh di aplikasi File Manager HP Anda.
2. Jika muncul peringatan *"Demi keamanan, ponsel Anda tidak diizinkan memasang aplikasi yang tidak dikenal dari sumber ini"*:
   - Ketuk **Setelan / Pengaturan**.
   - Aktifkan tombol **Izinkan dari sumber ini**.
3. Ketuk **Instal** dan tunggu hingga proses selesai.

---

## ⚙️ Mengaktifkan Izin yang Dibutuhkan

Untuk menggunakan fitur **Floating Control Panel**, Anda wajib memberikan izin **Tampilkan di atas aplikasi lain (Overlay)**:
1. Buka aplikasi **ZX SplitPlay**.
2. Masuk ke tab **Pengaturan** atau menu **Izin & Kebijakan Sistem**.
3. Ketuk tombol **"Berikan Izin di Pengaturan"**.
4. Sistem Android akan membuka setelan; aktifkan sakelar untuk **ZX SplitPlay**.
5. Tekan tombol Kembali untuk kembali ke aplikasi.

---

## 🎮 Panduan Manual Split-Screen Berdasarkan Merek HP

Jika sistem antarmuka ponsel Anda membatasi peluncuran otomatis melalui intent `FLAG_ACTIVITY_LAUNCH_ADJACENT`, gunakan langkah manual berikut:

- **Samsung Galaxy (One UI):**
  1. Buka game Anda.
  2. Masuk ke layar *Recent Apps* (Aplikasi Terbaru).
  3. Ketuk **ikon game** di bagian atas kartu pratinjau.
  4. Pilih **"Buka dalam tampilan layar pisah"**.
  5. Pilih aplikasi kedua di bagian bawah layar.

- **Xiaomi / Redmi / POCO (MIUI & HyperOS):**
  1. Buka game Anda, lalu buka layar *Recents*.
  2. Tekan dan tahan kartu aplikasi game hingga muncul bilah samping, lalu ketuk ikon **Dua Kotak (Split Screen)**.
  3. Atau buka fitur **Jendela Mengambang (Floating Window)** dari Game Turbo.

- **OPPO / Realme / OnePlus (ColorOS / Realme UI):**
  1. Di dalam game, geser ke atas layar menggunakan **3 jari** secara bersamaan.
  2. Game akan berpindah ke setengah layar atas, lalu pilih aplikasi kedua.

- **Vivo / iQOO (FuntouchOS):**
  1. Geser ke bawah layar dengan 3 jari untuk mengaktifkan **Smart Split**.

- **Google Pixel / Motorola / Stock Android:**
  1. Buka layar *Recent Apps*.
  2. Ketuk ikon aplikasi di atas kartu pratinjau game.
  3. Pilih **Layar Pisah (Split Screen)**.

> **Tips Game Menolak Layar Pisah:**  
> Jika game Anda menampilkan pesan *"Aplikasi tidak mendukung layar pisah"*, buka **Pengaturan HP > Opsi Pengembang (Developer Options)**, cari dan aktifkan opsi **"Paksa aktivitas dapat diubah ukuran" (Force activities to be resizable)**, lalu restart HP Anda.

---

## ⚠️ Batasan Perangkat & Kebijakan Sistem

1. **Bukan Universal Hack:** ZX SplitPlay mematuhi API resmi Android. Kami tidak menggunakan eksploitasi root atau memanipulasi kernel.
2. **Overlay Pihak Ketiga:** Sesuai kebijakan keamanan Android, aplikasi pihak ketiga tidak dapat menampilkan game atau aplikasi luar secara sewenang-wenang di dalam jendela mengambang mini biasa tanpa akses sistem vendor. Floating Panel kami berfungsi sebagai pusat kendali dan peluncur terintegrasi.
3. **RAM & Performa:** Menjalankan dua aplikasi grafis berat secara bersamaan membutuhkan kapasitas RAM yang cukup (disarankan minimal 4 GB RAM).

---

## 🧪 Pengujian & Build Mandiri (Lokal)

Jika Anda ingin menguji dan membangun APK dari komputer secara mandiri:

```bash
# Menjalankan unit test Robolectric
./gradlew testDebugUnitTest

# Membangun APK Debug
./gradlew assembleDebug
```

File APK hasil build akan berlokasi di:  
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🐛 Pelaporan Bug

Jika Anda menemukan kendala saat peluncuran atau ketidakcocokan pada tipe HP tertentu, silakan buat laporan di tab **GitHub Issues** dengan mencantumkan:
- Merek & Model HP (misal: POCO X3 Pro, Samsung A54)
- Versi Android (misal: Android 13, One UI 5.1)
- Nama Game dan Aplikasi Kedua yang dicoba
