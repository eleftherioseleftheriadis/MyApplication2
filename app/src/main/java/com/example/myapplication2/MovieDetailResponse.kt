package com.example.myapplication2

data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val overview: String,
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)
