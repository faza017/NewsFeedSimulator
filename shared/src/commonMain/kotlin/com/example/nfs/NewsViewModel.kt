package com.example.nfs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    // --- Requirement 4: StateFlow untuk menyimpan jumlah berita yang sudah dibaca ---
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    private val _readNewsIds = MutableStateFlow<Set<Int>>(emptySet())
    val readNewsIds: StateFlow<Set<Int>> = _readNewsIds.asStateFlow()

    // Filter Kategori State
    private val _selectedCategory = MutableStateFlow(NewsCategory.ALL)
    val selectedCategory: StateFlow<NewsCategory> = _selectedCategory.asStateFlow()

    // List Berita
    private val _newsFeedList = MutableStateFlow<List<NewsUiModel>>(emptyList())
    val newsFeedList: StateFlow<List<NewsUiModel>> = _newsFeedList.asStateFlow()

    // Detail Berita State
    private val _selectedNewsDetail = MutableStateFlow<String?>(null)
    val selectedNewsDetail: StateFlow<String?> = _selectedNewsDetail.asStateFlow()

    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

    private var newsCollectorJob: Job? = null

    init {
        startNewsFeedCollector()
    }

    // --- Requirement 1: Flow Builder & emit setiap 2 detik ---
    private fun getRawNewsFlow(category: NewsCategory): Flow<News> = flow {
        val techTitles = listOf(
            "Peluncuran Fitur AI Terbaru di Smartphone",
            "Inovasi Chipset Terbaru Lebih Hemat Daya 40%",
            "Framework Kotlin Multiplatform Rilis Versi Stable",
            "Kuantum Komputer Pertama Resmi Diuji Coba"
        )
        val sportsTitles = listOf(
            "Timnas Indonesia Memenangkan Pertandingan Final",
            "Turnamen Badminton Internasional Resmi Dimulai",
            "Klub Lokal Raih Gelar Juara Liga Musim Ini",
            "Rekor Dunia Lari 100m Berhasil Dipecahkan"
        )
        val businessTitles = listOf(
            "Pasar Saham Mengalami Peningkatan Positif",
            "Pertumbuhan Ekonomi Kuartal Ini Melampaui Target",
            "Investasi Sektor Teknologi Hijau Meningkat 50%",
            "Perusahaan Startup Lokal Raih Pendanaan Seri B"
        )

        var index = 0
        while (true) {
            delay(2000) // Emit data berita baru tepat setiap 2 detik

            val targetCategory = if (category == NewsCategory.ALL) {
                when (index % 3) {
                    0 -> NewsCategory.TECH
                    1 -> NewsCategory.SPORTS
                    else -> NewsCategory.BUSINESS
                }
            } else {
                category
            }

            val titleList = when (targetCategory) {
                NewsCategory.TECH -> techTitles
                NewsCategory.SPORTS -> sportsTitles
                NewsCategory.BUSINESS -> businessTitles
                else -> techTitles
            }

            val news = News(
                id = index + 1,
                title = titleList[index % titleList.size],
                content = "Isi berita simulasi untuk kategori ${targetCategory.displayName}. Data di-emit secara otomatis dari Flow setiap 2 detik.",
                category = targetCategory,
                timestamp = 1000L * (index + 1)
            )
            emit(news)
            index++
        }
    }

    // --- Requirement 2 & 3: Operators (filter, map, onEach, collect) ---
    private fun startNewsFeedCollector() {
        newsCollectorJob?.cancel()
        newsCollectorJob = viewModelScope.launch {
            val currentCategory = _selectedCategory.value
            _newsFeedList.value = emptyList()

            getRawNewsFlow(currentCategory)
                // Operator 1: filter berdasarkan kategori
                .filter { news ->
                    currentCategory == NewsCategory.ALL || news.category == currentCategory
                }
                // Operator 2: map transformasi data ke format tampilan (NewsUiModel)
                .map { news ->
                    NewsUiModel(
                        id = news.id,
                        title = news.title,
                        displayContent = news.content,
                        category = news.category,
                        formattedTime = "Berita #${news.id} • ${news.category.displayName}"
                    )
                }
                // Operator 3: onEach untuk side-effect
                .onEach { uiModel ->
                    println("Side-effect [onEach]: Emitted berita #${uiModel.id} - ${uiModel.title}")
                }
                // Terminal operator: collect
                .collect { uiModel ->
                    _newsFeedList.value = listOf(uiModel) + _newsFeedList.value
                }
        }
    }

    fun setCategory(category: NewsCategory) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            startNewsFeedCollector()
        }
    }

    // --- Requirement 4: Update StateFlow berita yang dibaca ---
    fun markAsRead(newsId: Int) {
        if (!_readNewsIds.value.contains(newsId)) {
            _readNewsIds.value = _readNewsIds.value + newsId
            _readCount.value = _readNewsIds.value.size
        }
    }

    // --- Requirement 5: Coroutines async/await & Dispatcher ---
    fun loadNewsDetail(news: NewsUiModel) {
        markAsRead(news.id)
        viewModelScope.launch {
            _isLoadingDetail.value = true
            _selectedNewsDetail.value = null

            // Mengambil detail berita secara asynchronous menggunakan async/await dan Dispatcher
            val detail = fetchDetailAsync(news)
            _selectedNewsDetail.value = detail
            _isLoadingDetail.value = false
        }
    }

    private suspend fun fetchDetailAsync(news: NewsUiModel): String = coroutineScope {
        val deferred = async(Dispatchers.Default) {
            delay(1000) // Simulasi pengambilan detail berita secara async
            "=== DETAIL BERITA (#${news.id}) ===\n\n" +
                    "Judul: ${news.title}\n" +
                    "Kategori: ${news.category.displayName}\n\n" +
                    "Isi Berita Lengkap:\n${news.displayContent}\n\n" +
                    "Status: Detail dimuat secara asynchronous dengan Coroutines async/await di Dispatchers.Default."
        }
        deferred.await()
    }

    fun clearNewsDetail() {
        _selectedNewsDetail.value = null
    }
}
