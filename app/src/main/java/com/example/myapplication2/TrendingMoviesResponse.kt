package com.example.myapplication2

data class TrendingMoviesResponse(val results: List<Movie>)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val genreIds: List<Int> // Note: This is simplified. TMDB uses genre IDs which you'd map to genre names.
)
