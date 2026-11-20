package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaseauth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ActivityGoogleMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class GoogleMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoogleMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGoogleMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        getGoogleData()

        binding.signOutBtn.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                // Ignore clear credential exception
            }
            val intent = Intent(this@GoogleMainActivity, SignInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun getGoogleData() {
        var email = intent.getStringExtra("email")
        var name = intent.getStringExtra("name")

        if (email.isNullOrEmpty()) {
            email = firebaseAuth.currentUser?.email
        }
        if (name.isNullOrEmpty()) {
            name = firebaseAuth.currentUser?.displayName
        }

        binding.textView.text = "Email: ${email ?: ""}\nName: ${name ?: ""}"
    }
}