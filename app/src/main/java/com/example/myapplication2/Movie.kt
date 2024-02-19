package com.example.myapplication2

data class Movies(
    val id: Int,
    val title: String,
    val overview: String,
    val genreIds: List<Int>?,
    val posterPath: String,
    var isLiked: Boolean = false,
    var isWatched: Boolean = false
)
