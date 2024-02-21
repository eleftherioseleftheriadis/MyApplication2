package com.example.myapplication2

data class TrendingMoviesResponse(val results: List<Movie>)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genreIds: List<Int>?,
    val posterPath: String,
    var isLiked: Boolean = true,
    var isWatched: Boolean = false
)
