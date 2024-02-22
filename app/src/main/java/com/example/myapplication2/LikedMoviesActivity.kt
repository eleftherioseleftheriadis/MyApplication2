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
import android.widget.Toast
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

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
           // fetchRecommendations()
        }


        fetchLikedMovies()
    }
   // private fun fetchRecommendations(prompt: String) {
     //   val chatGPTRequest = ChatGPTRequest(prompt = prompt)
        // Assume retrofit setup for ChatGPT API is done
     //   val chatGPTService = Retrofit.Builder() // Add your Retrofit builder setup
      //      .build()
      //      .create(ChatGPTApiService::class.java)

//        chatGPTService.getRecommendations(chatGPTRequest).enqueue(object : Callback<ChatGPTResponse> {
            //override fun onResponse(call: Call<ChatGPTResponse>, response: Response<ChatGPTResponse>) {
             //   val recommendations = response.body()?.choices?.firstOrNull()?.text ?: ""
              //  fetchMoviesFromTMDB(recommendations)
           // }

            //override fun onFailure(call: Call<ChatGPTResponse>, t: Throwable) {
              //  Log.e("API Error", "Network error getting recommendations", t)
         //   }
       // })
        //private fun fetchMoviesFromTMDB(recommendations: String) {
            // Parse recommendations and query TMDB for movies
            // Similar Retrofit call setup as for ChatGPT, adjusted for TMDB's API
       // }
       // private fun generatePromptFromLikedMovies(likedMoviesTitles: List<String>): String {
        //    return if (likedMoviesTitles.isEmpty()) {
         //       "I'm looking for movie recommendations. What do you suggest?"
          //  } else {
           //     "I like movies such as ${likedMoviesTitles.joinToString(", ")}. Based on these, what other movies would you recommend?"
          //  }
        //}
   // }






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
