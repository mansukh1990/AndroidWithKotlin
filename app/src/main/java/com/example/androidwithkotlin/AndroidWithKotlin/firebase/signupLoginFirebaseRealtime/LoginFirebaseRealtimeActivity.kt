package com.example.androidwithkotlin.AndroidWithKotlin.firebase.signupLoginFirebaseRealtime

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivityLoginFirebaseRealtimeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class LoginFirebaseRealtimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginFirebaseRealtimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            val intent = Intent(this, ProfileFirebaseRealtimeActivity::class.java).apply {
                putExtra("name", sharedPreferences.getString("name", ""))
                putExtra("email", sharedPreferences.getString("email", ""))
                putExtra("username", sharedPreferences.getString("username", ""))
                putExtra("password", sharedPreferences.getString("password", ""))
            }
            startActivity(intent)
            finish()
            return
        }

        enableEdgeToEdge()
        binding = ActivityLoginFirebaseRealtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupRedirectText.setOnClickListener {
            val intent = Intent(this, SigninFirebaseRealtimeActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            if (validateUsername() && validatePassword()) {
                checkUser()
            }
        }
    }

    fun validateUsername(): Boolean {
        val username = binding.loginUsername.text.toString()
        if (username.isEmpty()) {
            binding.loginUsername.error = "Username cannot be empty"
            return false
        } else {
            binding.loginUsername.error = null
            return true
        }
    }

    fun validatePassword(): Boolean {
        val userPassword = binding.loginPassword.text.toString()
        if (userPassword.isEmpty()) {
            binding.loginPassword.error = "Password cannot be empty"
            return false
        } else {
            binding.loginPassword.error = null
            return true
        }
    }

    fun checkUser() {
        val userUsername = binding.loginUsername.text.toString().trim()
        val userPassword = binding.loginPassword.text.toString().trim()

        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val checkUserDatabase: Query = reference.orderByChild("username").equalTo(userUsername)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.loginUsername.error = null

                    val userSnapshot = if (snapshot.hasChild(userUsername)) {
                        snapshot.child(userUsername)
                    } else {
                        snapshot.children.firstOrNull()
                    }

                    if (userSnapshot != null) {
                        val passwordFromDB =
                            userSnapshot.child("password").getValue(String::class.java)

                        if (passwordFromDB == userPassword) {
                            binding.loginPassword.error = null

                            val nameFromDB = userSnapshot.child("name").getValue(String::class.java)
                            val emailFromDB =
                                userSnapshot.child("email").getValue(String::class.java)
                            val usernameFromDB =
                                userSnapshot.child("username").getValue(String::class.java)

                            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                            sharedPreferences.edit().apply {
                                putBoolean("isLoggedIn", true)
                                putString("name", nameFromDB)
                                putString("email", emailFromDB)
                                putString("username", usernameFromDB)
                                putString("password", passwordFromDB)
                                apply()
                            }

                            val intent = Intent(this@LoginFirebaseRealtimeActivity, ProfileFirebaseRealtimeActivity::class.java).apply {
                                putExtra("name", nameFromDB)
                                putExtra("email", emailFromDB)
                                putExtra("username", usernameFromDB)
                                putExtra("password", passwordFromDB)
                            }

                            startActivity(intent)
                            finish()
                        } else {
                            binding.loginPassword.error = "Invalid Credentials"
                            binding.loginPassword.requestFocus()
                        }
                    }
                } else {
                    binding.loginUsername.error = "User does not exist"
                    binding.loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}