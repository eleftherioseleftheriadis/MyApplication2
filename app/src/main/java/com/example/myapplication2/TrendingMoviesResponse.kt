package com.example.myapplication2
import com.google.gson.annotations.SerializedName

data class TrendingMoviesResponse(val results: List<Movie>)
data class SearchMoviesResponse(
    @SerializedName("results") val results: List<Movie>
)
data class TMDBSearchResponse(
    @SerializedName("results") val results: List<Movie>
)

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class Movie @JvmOverloads constructor(
    var id: Int = 0,
    var title: String = "",
    var overview: String = "",
    @SerializedName("genres") val genres: List<Genre>? = null,
    @SerializedName("poster_path") var posterPath: String? = null,
    var isLiked: Boolean = false,
    var isWatched: Boolean = false
)
