package com.example.myapplication2
import com.google.gson.annotations.SerializedName

data class TrendingMoviesResponse(val results: List<Movie>)

data class Movie @JvmOverloads constructor(
    var id: Int = 0,
    var title: String = "",
    var overview: String = "",
    @SerializedName("genreIds")var genreIds: List<Int>? = null,
    @SerializedName("poster_path") var posterPath: String? = null,
    var isLiked: Boolean = false,
    var isWatched: Boolean = false
)
