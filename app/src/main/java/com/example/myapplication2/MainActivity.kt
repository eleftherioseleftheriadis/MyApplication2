package com.example.myapplication2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication2.ui.theme.MyApplication2Theme
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