import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication2.R
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication2.MainActivity

class LoginActivity : AppCompatActivity() {

private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.res.layout.activity_login)

        auth = Firebase.auth

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
        val email = findViewById<EditText>(R.id.emailEditText).text.toString()
        val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
        auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
        // Sign in success
        Log.d("LoginActivity", "signInWithEmail:success")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        } else {
        // If sign in fails, display a message to the user.
        Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
        Toast.makeText(baseContext, "Authentication failed.",
        Toast.LENGTH_SHORT).show()
        }
        }
        } else {
        Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
        }
        }
        }
        }
