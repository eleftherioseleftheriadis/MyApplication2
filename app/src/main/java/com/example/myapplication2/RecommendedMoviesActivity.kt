package com.example.myapplication2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RecommendedMoviesActivity : AppCompatActivity() {
    private lateinit var recommendedMoviesRecyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended_movies)

        recommendedMoviesRecyclerView = findViewById(R.id.recommendedMoviesRecyclerView)
        recommendedMoviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list. Adjust based on your actual implementation.
        moviesAdapter = MoviesAdapter(AppGlobals.GlobalMoviesList,
            onMovieClick = { movie ->
                Toast.makeText(this, "Movie clicked: ${movie.title}", Toast.LENGTH_LONG).show()

            },
            onLikeClick = { movie ->
                movie.isLiked = !movie.isLiked
                if (movie.isLiked) {
                    //saveLikedMovie(movie)
                } else {
                    removeLikedMovie(movie)
                }
                moviesAdapter.notifyDataSetChanged()
            }
        )

        recommendedMoviesRecyclerView.adapter = moviesAdapter

        // Extract movie titles from intent extras
        val movieTitles = intent.getStringArrayListExtra("movieTitles") ?: arrayListOf()

        // Fetch movie details for each title from TMDB and update UI
        fetchMovieDetailsFromTMDB(movieTitles)
    }

    private fun fetchMovieDetailsFromTMDB(movieTitles: List<String>) {
        val service = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApi::class.java) // Ensure you have a Retrofit interface for TMDB API

        // Iterate over the movie titles to fetch details for each. Simplified for example purposes.
        movieTitles.forEach { title ->
            service.searchMovies(apiKey = "735fb60abd35639daa0561b46481d912", query = title).enqueue(object : retrofit2.Callback<SearchMoviesResponse> {
                override fun onResponse(call: retrofit2.Call<SearchMoviesResponse>, response: retrofit2.Response<SearchMoviesResponse>) {
                    if (response.isSuccessful) {
                        // Assuming your response includes a list of movies
                        val movies = response.body()?.results ?: listOf()
                        runOnUiThread {
                            moviesAdapter.updateMovies(movies)
                        }
                    } else {
                        Log.e("TMDB API", "Error fetching movie details")
                    }
                }

                override fun onFailure(call: retrofit2.Call<SearchMoviesResponse>, t: Throwable) {
                    Log.e("TMDB API", "Failed to fetch movie details", t)
                }
            })
        }
    }
    private fun removeLikedMovie(movie: Movie) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.w("Firestore", "User not logged in")
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("likedMovies")
            .document(movie.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Movie successfully removed from liked movies")
                AppGlobals.GlobalMoviesList.removeAll { it.id == movie.id }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error removing movie from liked movies", e)
            }
    }

    // Implement other necessary methods and classes as per your application's requirement.
}
