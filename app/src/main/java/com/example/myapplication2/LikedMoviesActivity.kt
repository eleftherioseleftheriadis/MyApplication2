package com.example.myapplication2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LikedMoviesActivity : AppCompatActivity() {
    private lateinit var likedMoviesRecyclerView: RecyclerView
    // Use consistent naming for the adapter
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_movies)

        likedMoviesRecyclerView = findViewById(R.id.likedMoviesRecyclerView)
        likedMoviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with empty list and an onClick lambda, even if it does nothing for now
        moviesAdapter = MoviesAdapter(mutableListOf()) { movie ->
            // Implement action on movie click if necessary, for example, navigating to a movie detail page
        }
        likedMoviesRecyclerView.adapter = moviesAdapter

        updateLikedMovies()
    }

    private fun updateLikedMovies() {
        // Use the existing moviesAdapter variable
        val likedMovies = AppGlobals.GlobalMoviesList.filter { it.isLiked }
        // Instead of creating a new MoviesAdapter, update the existing adapter's data
        moviesAdapter.updateMovies(likedMovies.toMutableList())
        // No need to set the adapter again since we're updating the existing one
    }
}
