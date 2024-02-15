package com.example.myapplication2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface TMDBApi {
    @GET("movie/{movie_id}")
    fun getTrendingMovies(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") extras: String = "images,genres"
    ): Call<MovieDetailResponse>

}