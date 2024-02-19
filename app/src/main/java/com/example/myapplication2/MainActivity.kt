package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Button

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
        moviesAdapter = MoviesAdapter(mutableListOf()) { movie ->
            movie.isLiked = !movie.isLiked
            moviesAdapter.notifyDataSetChanged()
            // Call refreshMoviesList here if you need to update the global list or UI immediately after a like action
        }
        moviesRecyclerView.adapter = moviesAdapter

        fetchTrendingMovies() // Call this to fetch and display trending movies

        // Show liked movies when the button is clicked
        findViewById<Button>(R.id.btnShowLikedMovies).setOnClickListener {
            showLikedMovies()
        }
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
                    AppGlobals.GlobalMoviesList.clear()
                    AppGlobals.GlobalMoviesList.addAll(movies)
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

    private fun showLikedMovies() {
        val intent = Intent(this, LikedMoviesActivity::class.java)
        startActivity(intent)
    }

    private fun refreshMoviesList() {
        val currentMovies = AppGlobals.GlobalMoviesList
        moviesAdapter.updateMovies(currentMovies)
    }

    override fun onResume() {
        super.onResume()
        refreshMoviesList() // Refresh the movies list to reflect any changes made elsewhere.
    }
}
