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
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import org.json.JSONObject
import org.json.JSONException






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
            .addHeader("Authorization", "Bearer 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") // Use your actual API key
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("ChatGPT API", "Network error", e)
            }



                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    Log.e("ChatGPT API", "Response not successful: ${response.code}")

                    return
                }

                val responseBody = response.body?.string() ?: ""
                Log.d("ChatGPT API", "Full API Response: $responseBody")

                // Now parse the JSON to extract the recommendations
                    try {
                        val jsonObj = JSONObject(responseBody)
                        val choices = jsonObj.getJSONArray("choices")
                        if (choices.length() > 0) {
                            val firstChoice = choices.getJSONObject(0)
                            val message = firstChoice.getJSONObject("message")
                            val content = message.getString("content")
                            val jsonContent = JSONObject(content)
                            val recommendations = jsonContent.getJSONArray("recommendations")

                            val movieTitles = ArrayList<String>()
                            for (i in 0 until recommendations.length()) {
                                val recommendation = recommendations.getJSONObject(i)
                                val title = recommendation.getString("title")
                                movieTitles.add(title)
                            }

                            Log.d("LikedListActivity", "Preparing to show recommended movies with titles: $movieTitles")
                            if (movieTitles.isNotEmpty()) {
                                runOnUiThread {
                                    // Make sure to call this method on the UI thread if it interacts with the UI
                                    showRecommendedMovies(movieTitles)
                                }
                            } else {
                                Log.d("LikedListActivity", "No movie titles to show.")
                            }
                        }
                    } catch (e: JSONException) {
                        Log.e("ChatGPT API", "JSON parsing error", e)
                    }

                    catch (e: JSONException) {
                    Log.e("ChatGPT API", "JSON parsing error", e)
                }

                catch (e: JSONException) {
                    Log.e("ChatGPT API", "Failed to parse JSON response", e)
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



    // Dummy implementation for parsing - replace with actual parsing logic
    private fun parseRecommendationsToSearchKeywords(recommendations: String): List<String> {
        // Implement parsing logic here. For now, let's just return the whole recommendations string in a list
        return recommendations.split(",").map { it.trim() }
    }

    private fun showRecommendedMovies(movieTitles: ArrayList<String>) {
        Log.d("LikedListActivity", "Movie titles before intent: $movieTitles")
        val intent = Intent(this, RecommendedMoviesActivity::class.java).apply {
            Log.d("LikedListActivity", "Movie titles before intent: $movieTitles")
            putStringArrayListExtra("movieTitles", movieTitles)
        }
        startActivity(intent)
    }


    private fun generatePromptFromLikedMovies(likedMoviesTitles: List<String>): String {
        return if (likedMoviesTitles.isEmpty()) {
            "I'm looking for movie recommendations. What do you suggest?"
        } else {
            "I like movies such as ${likedMoviesTitles.joinToString(", ")}. Based on these, recommend 2 movies give me only titles and in json format"
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
