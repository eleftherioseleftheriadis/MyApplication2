package com.example.myapplication2
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapplication2.R
import com.example.myapplication2.TMDBApi
import com.example.myapplication2.TrendingMoviesResponse
import com.example.myapplication2.Movie

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var moviesTextView: TextView
    private val apiKey = "735fb60abd35639daa0561b46481d912" // Replace with your actual TMDB API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        moviesTextView = findViewById(R.id.moviesTextView)
        fetchTrendingMovies()
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
                    updateMoviesTextView(movies)
                } else {
                    moviesTextView.text = "Failed to fetch movies" // Direct string literal
                }
            }

            override fun onFailure(call: Call<TrendingMoviesResponse>, t: Throwable) {
                moviesTextView.text = "Error fetching movies: ${t.message}" // Direct string literal with interpolation
            }
        })
    }

    private fun updateMoviesTextView(movies: List<Movie>) {
        val movieTitles = movies.joinToString(separator = "\n") { it.title }
        moviesTextView.text = movieTitles
    }
}
