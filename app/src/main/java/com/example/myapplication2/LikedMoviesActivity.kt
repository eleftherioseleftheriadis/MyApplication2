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

class LikedMoviesActivity : AppCompatActivity() {
    private lateinit var likedMoviesRecyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_movies)

        likedMoviesRecyclerView = findViewById(R.id.likedMoviesRecyclerView)
        likedMoviesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list and an onClick lambda
        moviesAdapter = MoviesAdapter(mutableListOf()) { movie ->
            // Implement action for movie click if necessary
        }

        likedMoviesRecyclerView.adapter = moviesAdapter

        // Set the OnClickListener for the button after setContentView
        findViewById<Button>(R.id.btnGoToMainOrSignOut).setOnClickListener {
            // Intent to go back to MainActivity
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        fetchLikedMovies()
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
                moviesAdapter.updateMovies(likedMovies.toMutableList())
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting liked movies: ", exception)
            }
    }
}
