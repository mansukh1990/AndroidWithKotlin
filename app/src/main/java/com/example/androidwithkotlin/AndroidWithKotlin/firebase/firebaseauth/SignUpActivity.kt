package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.txtSignin.setOnClickListener {
            gotoSignInActivity()
        }

        binding.buttonSignup.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passET.text.toString().trim()
            val confirmPass = binding.confirmPassEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.signOut()
                            Toast.makeText(this, "Sign up successful! Please sign in.", Toast.LENGTH_SHORT).show()
                            gotoSignInActivity(email)
                        } else {
                            Toast.makeText(this, task.exception?.message ?: "Sign up failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (confirmPass.isEmpty()) {
                Toast.makeText(this, "Confirm Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gotoSignInActivity(email: String = "") {
        val intent = Intent(this, SignInActivity::class.java).apply {
            if (email.isNotEmpty()) {
                putExtra("email", email)
            }
        }
        startActivity(intent)
        finish()
    }
}