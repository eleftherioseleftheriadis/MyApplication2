package com.example.myapplication2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.myapplication2.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        val registerButton: Button = findViewById(R.id.registerButton)
        registerButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            // Basic validation
            if (email.isNotEmpty() && password.length >= 6) {
                // Create a new user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("RegisterActivity", "createUserWithEmail:success")
                            val user = auth.currentUser
                            // Redirect to MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Call finish() if you do not want users to return to this activity on back press
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }

                    }
            } else {
                Toast.makeText(this, "Email/password must not be empty and password should be at least 6 characters", Toast.LENGTH_LONG).show()
            }
        }
    }
}

