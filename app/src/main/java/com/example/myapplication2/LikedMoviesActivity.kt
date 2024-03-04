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
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import com.google.gson.Gson






class LikedMoviesActivity : AppCompatActivity() {
    private lateinit var likedMoviesRecyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_movies)

        likedMoviesRecyclerView = findViewById(R.id.likedMoviesRecyclerView)
        likedMoviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list and an onClick lambda
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


        likedMoviesRecyclerView.adapter = moviesAdapter

        // Set the OnClickListener for the button after setContentView
        findViewById<Button>(R.id.btnGoToMainOrSignOut).setOnClickListener {
            // Intent to go back to MainActivity
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
        val recommendationsButton: Button = findViewById(R.id.btnGetRecommendations)
        recommendationsButton.setOnClickListener {
            Toast.makeText(this, "Fetching recommendations...", Toast.LENGTH_SHORT).show()
            fetchLikedMoviesTitlesAndGeneratePrompt()
        }


        fetchLikedMovies()
    }
    private fun fetchRecommendations(prompt: String) {
        val client = OkHttpClient()


        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonPayload = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [
                {
                    "role": "user",
                    "content": "$prompt"
                }
            ],
            "temperature": 0.5,
            "max_tokens": 500
        }
    """.trimIndent()
        Log.d("ChatGPT API", "Sending request to ChatGPT with prompt: $prompt")
        val requestBody = jsonPayload.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer ") // Replace YOUR_API_KEY with your actual API key
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure here
                Log.e("ChatGPT API", "Network error", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("ChatGPT API", "Response not successful: ${response.code}")
                    return
                }

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    // Use Gson to parse the JSON response
                    val gson = Gson()
                    val chatGPTResponse = gson.fromJson(responseBody, ChatGPTResponse::class.java)
                    val recommendations = chatGPTResponse.choices.firstOrNull()?.text?.trim()

                    if (recommendations != null) {
                        Log.d("ChatGPT API", "Recommendations received: $recommendations")
                        // Process the recommendations here
                    } else {
                        Log.d("ChatGPT API", "No recommendations found.")
                    }
                } else {
                    Log.e("ChatGPT API", "Response body is null")
                }
            }

        })
    }



    private fun fetchLikedMoviesTitlesAndGeneratePrompt() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("likedMovies")
            .get()
            .addOnSuccessListener { documents ->
                val likedMoviesTitles = documents.mapNotNull { it.toObject(Movie::class.java).title }
                val prompt = generatePromptFromLikedMovies(likedMoviesTitles)
                fetchRecommendations(prompt) // Assuming fetchRecommendations now takes a prompt as a parameter
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting liked movies: ", exception)
            }
    }

    private fun fetchMoviesFromTMDB(recommendations: String) {
        // Assume you have a Retrofit service for TMDB set up similar to this:
        val service = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBApi::class.java)

        // Parse the recommendations to extract search keywords or titles
        val searchKeywords = parseRecommendationsToSearchKeywords(recommendations)

        // For simplicity, let's assume parseRecommendationsToSearchKeywords returns a list of keywords
        searchKeywords.forEach { keyword ->
            service.searchMovies(apiKey = "735fb60abd35639daa0561b46481d912", query = keyword).enqueue(object : retrofit2.Callback<SearchMoviesResponse> {
                override fun onResponse(call: Call<SearchMoviesResponse>, response: Response<SearchMoviesResponse>) {
                    if (response.isSuccessful) {
                        val movies = response.body()?.results ?: emptyList()
                        // Update your UI with these movies
                        // For example, adding them to a list in your adapter
                        runOnUiThread {
                            moviesAdapter.updateMovies(movies.toMutableList())
                        }
                    } else {
                        Log.e("TMDB API", "Error fetching movies: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<SearchMoviesResponse>, t: Throwable) {
                    Log.e("TMDB API", "Network error getting movies", t)
                }
            })
        }
    }

    // Dummy implementation for parsing - replace with actual parsing logic
    private fun parseRecommendationsToSearchKeywords(recommendations: String): List<String> {
        // Implement parsing logic here. For now, let's just return the whole recommendations string in a list
        return recommendations.split(",").map { it.trim() }
    }
    private fun generatePromptFromLikedMovies(likedMoviesTitles: List<String>): String {
        return if (likedMoviesTitles.isEmpty()) {
            "I'm looking for movie recommendations. What do you suggest?"
        } else {
            "I like movies such as ${likedMoviesTitles.joinToString(", ")}. Based on these, recommend 2 movies"
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
