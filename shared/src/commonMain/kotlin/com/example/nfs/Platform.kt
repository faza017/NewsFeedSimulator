package com.example.nfs

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform