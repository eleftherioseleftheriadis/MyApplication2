package com.example.myapplication2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.Button
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var moviesRecyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter
    private val apiKey = "735fb60abd35639daa0561b46481d912" // Replace with your actual TMDB API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        moviesRecyclerView = findViewById(R.id.moviesRecyclerView)
        moviesRecyclerView.layoutManager = LinearLayoutManager(this)
        moviesAdapter = MoviesAdapter(AppGlobals.GlobalMoviesList) { movie ->
            movie.isLiked = !movie.isLiked
            AppGlobals.GlobalMoviesList.find { it.id == movie.id }?.isLiked = movie.isLiked

            if (movie.isLiked) {
                saveLikedMovie(movie) // Save liked movie when it's liked
            }
            moviesAdapter.notifyDataSetChanged()
        }
        moviesRecyclerView.adapter = moviesAdapter

        fetchTrendingMovies() // Implement this method to fetch movies

        findViewById<Button>(R.id.btnShowLikedMovies).setOnClickListener {
            // Implement navigation to LikedMoviesActivity
        }
    }

    private fun saveLikedMovie(movie: Movie) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val movieData = hashMapOf(
            "id" to movie.id,
            "title" to movie.title,
            "overview" to movie.overview,
            "genreIds" to movie.genreIds,
            "posterPath" to movie.posterPath,
            "isLiked" to movie.isLiked,
            "isWatched" to movie.isWatched
        )

        db.collection("users").document(userId).collection("likedMovies")
            .document(movie.id.toString())
            .set(movieData)
            .addOnSuccessListener {
                Log.d("Firestore", "Movie successfully added to liked movies!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding movie to liked movies", e)
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



    private fun refreshMoviesList() {
        val currentMovies = AppGlobals.GlobalMoviesList
        moviesAdapter.updateMovies(currentMovies)
    }

    override fun onResume() {
        super.onResume()
        refreshMoviesList() // Refresh the movies list to reflect any changes made elsewhere.
    }
}

