package com.example.myapplication2

data class TrendingMoviesResponse(val results: List<Movie>)

data class Movie @JvmOverloads constructor(
    var id: Int = 0,
    var title: String = "",
    var overview: String = "",
    var genreIds: List<Int>? = null,
    var posterPath: String? = null,
    var isLiked: Boolean = false,
    var isWatched: Boolean = false
)
