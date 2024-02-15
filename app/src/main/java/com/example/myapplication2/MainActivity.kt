package com.example.myapplication2

import android.os.Bundle
import com.google.firebase.FirebaseApp
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
class MainActivity : AppCompatActivity() {

    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val signOutButton = findViewById<Button>(R.id.signOutButton)

        // Display user email if logged in
        auth.currentUser?.let {
            welcomeText.text = "Welcome, ${it.email}"
        }

        signOutButton.setOnClickListener {
            auth.signOut()
            // Redirect to login activity after sign out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}