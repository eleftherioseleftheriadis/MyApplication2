package com.example.myapplication2

data class SMovie(
    val id: Int,
    val title: String,
    val summary: String,
    val genre: String,
    var isLiked: Boolean = false,
    var isWatched: Boolean = false
)
