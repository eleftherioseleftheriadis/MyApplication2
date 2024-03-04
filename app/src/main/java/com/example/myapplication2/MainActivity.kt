package com.example.myapplication2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }


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

        moviesAdapter = MoviesAdapter(AppGlobals.GlobalMoviesList,
            onMovieClick = { movie ->
                Toast.makeText(this, "Movie clicked: ${movie.title}", Toast.LENGTH_LONG).show()

            },
            onLikeClick = { movie ->
                movie.isLiked = !movie.isLiked
                if (movie.isLiked) {
                    saveLikedMovie(movie)
                } else {
                    removeLikedMovie(movie)
                }
                moviesAdapter.notifyDataSetChanged()
            }
        )

        moviesRecyclerView.adapter = moviesAdapter

        initButtons()
        fetchTrendingMovies()
    }


    private fun initButtons() {
        findViewById<Button>(R.id.signOutButton).setOnClickListener {
            showLogin()
        }
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            showRegister()
        }
        findViewById<Button>(R.id.btnShowLikedMovies).setOnClickListener {
            showLikedMovies()
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



    private fun saveLikedMovie(movie: Movie) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.w("Firestore", "User not logged in, cannot save movie")
            return
        }

        val movieData = hashMapOf(
            "id" to movie.id,
            "title" to movie.title,
            "overview" to movie.overview,
            "genreIds" to movie.genres,
            "posterPath" to movie.posterPath,
            "isLiked" to movie.isLiked,
            "isWatched" to movie.isWatched
        )

        val db = FirebaseFirestore.getInstance()
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
    private fun listenForLikedMoviesChanges(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("likedMovies")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d(TAG, "Current data: ${snapshot.documents}")
                    // Optionally, update your UI here based on the changes
                } else {
                    Log.d(TAG, "Current data: null")
                }
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
                    Log.d("MainActivity", "Number of movies fetched: ${movies.size}")
                    AppGlobals.GlobalMoviesList.clear()
                    AppGlobals.GlobalMoviesList.addAll(movies)
                    moviesAdapter.updateMovies(movies)
                } else {
                    Log.e("MainActivity", "Error fetching trending movies: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<TrendingMoviesResponse>, t: Throwable) {
                Log.e("Retrofit", "Error fetching trending movies: ", t)
            }
        })
    }

    private fun showLikedMovies() {
        val intent = Intent(this, LikedMoviesActivity::class.java)
        startActivity(intent)
    }

    private fun showLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun showRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        refreshMoviesList() // Refresh the movies list to reflect any changes made elsewhere.
    }



    private fun refreshMoviesList() {
        moviesAdapter.notifyDataSetChanged() // Simply notify the adapter to refresh the views.
    }
}