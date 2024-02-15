package com.example.myapplication2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyObjectList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter
    private val apiKey = "735fb60abd35639daa0561b46481d912" // Use your actual TMDB API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerView and its adapter
        moviesRecyclerView = findViewById(R.id.moviesRecyclerView)
        moviesRecyclerView.layoutManager = LinearLayoutManager(this)
        moviesAdapter = MoviesAdapter(mutableListOf()) // Initialize with empty list
        moviesRecyclerView.adapter = moviesAdapter

        fetchTrendingMovies() // Call this to fetch and display trending movies
    }

    private fun fetchTrendingMovies() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmdbApi = retrofit.create(TMDBApi::class.java)

        tmdbApi.getTrendingMovies(apiKey).enqueue(object : Callback<TrendingMoviesResponse> {
            override fun onResponse(call: Call<TrendingMoviesResponse>, response: Response<TrendingMoviesResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    moviesAdapter.updateMovies(movies) // Update the adapter's data
                } else {
                    // Handle failure
                }
            }

            override fun onFailure(call: Call<TrendingMoviesResponse>, t: Throwable) {
                // Handle error
            }
        })
    }
}

// Include your MoviesAdapter class here with an updateMovies method to set new data and notify the adapter of the change.
