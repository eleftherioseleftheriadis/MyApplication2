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
        // Redirect to login or main activity as needed
        } else {
        // If sign in fails, display a message to the user.
        Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
        Toast.makeText(baseContext, "Authentication failed.",
        Toast.LENGTH_SHORT).show()
        }
        }
        } else {
        Toast.makeText(this, "Email/password must not be empty and password should be at least 6 characters", Toast.LENGTH_LONG).show()
        }
        }
        }
        }
