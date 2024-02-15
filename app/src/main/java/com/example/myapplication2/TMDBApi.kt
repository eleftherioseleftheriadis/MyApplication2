package com.example.myapplication2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApi {
    @GET("trending/movie/day")
    fun getTrendingMovies(@Query("api_key") apiKey: String): Call<TrendingMoviesResponse>
}