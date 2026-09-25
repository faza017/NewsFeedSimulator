# News Feed Simulator

Aplikasi **News Feed Simulator** adalah aplikasi berbasis **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform** yang mensimulasikan aliran berita secara real-time (live stream), dengan filter kategori, pengolahan data menggunakan Kotlin Flow & Operators, pengelolaan state berita terbaca menggunakan StateFlow, serta pengambilan detail berita secara asynchronous menggunakan Kotlin Coroutines.

---

## Requirements Tugas
1. **Flow**: Simulasi data berita baru yang di-emit setiap 2 detik.
2. **Operators**: Memiliki filter berita berdasarkan kategori (`filter`), transformasi format tampilan (`map`), dan side-effect (`onEach`).
3. **StateFlow**: Menyimpan dan mengupdate jumlah berita yang sudah dibaca oleh pengguna.
4. **Coroutines**: Mengambil detail berita secara asynchronous menggunakan `async`/`await` dan dispatcher (`Dispatchers.Default`).
5. **Multiplatform UI**: Tampilan deklaratif menggunakan Compose Multiplatform yang berjalan di Android dan Desktop (JVM).

---

## Teknologi yang Digunakan
- **Kotlin Multiplatform (KMP)**
- **Compose Multiplatform (Jetpack Compose Multiplatform UI)**
- **Kotlin Coroutines & Flow** (`kotlinx-coroutines-core`)
- **Android Gradle Plugin (AGP)**

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

## Penjelasan Implementasi Kode

### 1. Implementasi Flow (Data Emitted Setiap 2 Detik)
Berita disimulasikan menggunakan `flow {}` builder dengan `delay(2000)` dan `emit()` pada `NewsViewModel.kt`:
```kotlin
private fun getRawNewsFlow(): Flow<News> = flow {
    var index = 0
    while (true) {
        delay(2000) // Emit berita baru setiap 2 detik
        val news = sampleNewsList[index % sampleNewsList.size].copy(
            id = index + 1,
            timestamp = 1000L * (index + 1)
        )
        emit(news)
        index++
    }
}
```

### 2. Implementasi Operators (`filter`, `map`, `onEach`, `collect`)
Alur pengolahan data berita diolah secara deklaratif dengan urutan operator:
```kotlin
getRawNewsFlow()
    // Operator filter: menyaring berita sesuai kategori yang dipilih
    .filter { news ->
        category == NewsCategory.ALL || news.category == category
    }
    // Operator map: mentransformasi objek News ke format UI (NewsUiModel)
    .map { news ->
        NewsUiModel(
            id = news.id,
            title = news.title,
            displayContent = news.content,
            category = news.category,
            formattedTime = "Berita #${news.id} • ${news.category.displayName}"
        )
    }
    // Operator onEach: menangani side-effect (logging)
    .onEach { uiModel ->
        println("Side-effect [onEach]: Emitted berita - ${uiModel.title}")
    }
    // Terminal Operator: mengumpulkan hasil data ke UI State List
    .collect { uiModel ->
        _newsFeedList.value = listOf(uiModel) + _newsFeedList.value
    }
```

### 3. Implementasi StateFlow (Jumlah Berita Terbaca)
Menggunakan `MutableStateFlow` dan `StateFlow` yang diekspos melalui `asStateFlow()` untuk mencatat jumlah berita unik yang dibaca:
```kotlin
private val _readCount = MutableStateFlow(0)
val readCount: StateFlow<Int> = _readCount.asStateFlow()

fun markAsRead(newsId: Int) {
    if (!_readNewsIds.value.contains(newsId)) {
        _readNewsIds.value = _readNewsIds.value + newsId
        _readCount.value = _readNewsIds.value.size
    }
}
```

### 4. Implementasi Coroutines (`async`, `await`, `Dispatcher`)
Mengambil detail berita secara asynchronous di thread latar belakang (`Dispatchers.Default`):
```kotlin
fun loadNewsDetail(news: NewsUiModel) {
    markAsRead(news.id)
    viewModelScope.launch {
        _isLoadingDetail.value = true
        val detail = fetchDetailAsync(news)
        _selectedNewsDetail.value = detail
        _isLoadingDetail.value = false
    }
}

private suspend fun fetchDetailAsync(news: NewsUiModel): String = coroutineScope {
    val deferred = async(Dispatchers.Default) {
        delay(1000) // Simulasi operasi async
        "=== DETAIL BERITA (#${news.id}) ===\n..."
    }
    deferred.await()
}
```
