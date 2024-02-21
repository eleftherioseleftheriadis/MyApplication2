package com.example.myapplication2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.widget.Button
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class LikedMoviesActivity : AppCompatActivity() {
    private lateinit var likedMoviesRecyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_movies)

        likedMoviesRecyclerView = findViewById(R.id.likedMoviesRecyclerView)
        likedMoviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list and an onClick lambda
        moviesAdapter = MoviesAdapter(mutableListOf(),
            onMovieClick = { movie ->
                // Handle movie click action here, if needed
            },
            onLikeClick = { movie ->
                // Handle like click action here, if needed
            }
        )


        likedMoviesRecyclerView.adapter = moviesAdapter

        // Set the OnClickListener for the button after setContentView
        findViewById<Button>(R.id.btnGoToMainOrSignOut).setOnClickListener {
            // Intent to go back to MainActivity
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
        val recommendationsButton: Button = findViewById(R.id.btnGetRecommendations)
        recommendationsButton.setOnClickListener {
            fetchRecommendations()
        }


        fetchLikedMovies()
    }
    private fun fetchRecommendations() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ChatGPTApiService::class.java)
        val call = service.getRecommendations("your_query_parameters")

        call.enqueue(object : retrofit2.Callback<RecommendationsResponse> {
            override fun onResponse(call: Call<RecommendationsResponse>, response: Response<RecommendationsResponse>) {
                if (response.isSuccessful) {
                    // Process the recommendations
                    val recommendations = response.body()?.recommendations ?: emptyList()
                    // Update your UI or logic based on recommendations
                } else {
                    Log.e("API Error", "Error fetching recommendations")
                }
            }

            override fun onFailure(call: Call<RecommendationsResponse>, t: Throwable) {
                Log.e("API Error", "Network error getting recommendations", t)
            }
        })
    }

    interface ChatGPTApiService {
        @GET("getRecommendations")
        fun getRecommendations(@Query("param") param: String): Call<RecommendationsResponse>
    }

    data class RecommendationsResponse(val recommendations: List<String>)


    private fun fetchLikedMovies() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).collection("likedMovies")
            .get()
            .addOnSuccessListener { documents ->
                val likedMovies = documents.mapNotNull { doc ->
                    doc.toObject(Movie::class.java)
                }
                // Just pass the new list of movies to the adapter
                moviesAdapter.updateMovies(likedMovies.toMutableList())
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting liked movies: ", exception)
            }
    }

}
