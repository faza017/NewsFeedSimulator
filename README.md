# News Feed Simulator

**Nama**: M Faza Prasetyo
**NIM**: 124140204
**Matkul**: PAM
**Tugas**: News Feed Simulator

Aplikasi **News Feed Simulator** adalah aplikasi berbasis **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform** yang mensimulasikan aliran berita secara real-time (live stream), dengan filter kategori, pengolahan data menggunakan Kotlin Flow & Operators, pengelolaan state berita terbaca menggunakan StateFlow, serta pengambilan detail berita secara asynchronous menggunakan Kotlin Coroutines.

---

## Requirements Tugas
1. **Flow**: Simulasi data berita baru yang di-emit setiap 2 detik.
2. **Operators**: Memiliki filter berita berdasarkan kategori (`filter`), transformasi format tampilan (`map`), dan side-effect (`onEach`).
3. **StateFlow**: Menyimpan dan mengupdate jumlah berita yang sudah dibaca oleh pengguna.
4. **Coroutines**: Mengambil detail berita secara asynchronous menggunakan `async`/`await` dan dispatcher (`Dispatchers.Default`).
5. **Multiplatform UI**: Tampilan deklaratif menggunakan Compose Multiplatform yang berjalan di Android dan Desktop (JVM).

---

## Cara Menjalankan Project

### 1. Menjalankan di Android
Gunakan perintah Gradle berikut pada terminal:
```bash
./gradlew :androidApp:assembleDebug
```
Atau jalankan konfigurasi `androidApp` langsung melalui tombol **Run (Shift+F10)** di Android Studio.

### 2. Menjalankan di Desktop (JVM)
Gunakan perintah Gradle berikut pada terminal:
```bash
./gradlew :desktopApp:run
```

---

