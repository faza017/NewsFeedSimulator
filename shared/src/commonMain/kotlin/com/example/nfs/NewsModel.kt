package com.example.nfs

enum class NewsCategory(val displayName: String) {
    ALL("Semua"),
    TECH("Teknologi"),
    SPORTS("Olahraga"),
    BUSINESS("Bisnis")
}

data class News(
    val id: Int,
    val title: String,
    val content: String,
    val category: NewsCategory,
    val timestamp: Long
)

data class NewsUiModel(
    val id: Int,
    val title: String,
    val displayContent: String,
    val category: NewsCategory,
    val formattedTime: String
)
