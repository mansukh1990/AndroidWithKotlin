package com.example.androidwithkotlin.AndroidWithKotlin.firebase.signupLoginFirebaseRealtime

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivitySigninFirebaseRealtimeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SigninFirebaseRealtimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninFirebaseRealtimeBinding
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySigninFirebaseRealtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginFirebaseRealtimeActivity::class.java)
            startActivity(intent)
        }

        binding.signupButton.setOnClickListener {
            database= FirebaseDatabase.getInstance()
            reference = database!!.getReference("Users")

            val name = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val username = binding.signupUsername.text.toString()
            val password = binding.signupPassword.text.toString()

            val signupRequest = SignupRequest(name, email, username, password)
            reference!!.child(username).setValue(signupRequest)

            Toast.makeText(this, "You have signup successfully!", Toast.LENGTH_SHORT).show();

            val intent = Intent(this, LoginFirebaseRealtimeActivity::class.java)
            startActivity(intent)
        }

    }
}