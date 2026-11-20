package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaseauth

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ActivitySignInBinding
import com.example.androidwithkotlin.databinding.DialogForgotBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        val emailExtra = intent.getStringExtra("email")
        if (!emailExtra.isNullOrEmpty()) {
            binding.emailEt.setText(emailExtra)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.forgotPassword.setOnClickListener {
            forgotPassword()
        }

        binding.txtSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, GoogleMainActivity::class.java).apply {
                            putExtra("email", email)
                            putExtra("name", pass)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message ?: "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun forgotPassword() {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogForgotBinding.inflate(layoutInflater)

        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.btnReset.setOnClickListener {
            compareEmail(dialogBinding.emailEt)
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        if (dialog.window != null) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    private fun signInWithGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@SignInActivity
                )
                handleSignInResult(result)
            } catch (e: GetCredentialException) {
                Toast.makeText(
                    this@SignInActivity,
                    e.message ?: "Google Sign In failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, GoogleMainActivity::class.java).apply {
                                putExtra("email", googleIdTokenCredential.id)
                                putExtra(
                                    "name",
                                    googleIdTokenCredential.displayName
                                        ?: googleIdTokenCredential.givenName ?: ""
                                )
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                task.exception?.message ?: "Firebase Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(this, e.message ?: "Invalid Google ID token", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, GoogleMainActivity::class.java).apply {
                putExtra("email", currentUser.email)
                putExtra("name", currentUser.displayName ?: "")
            }
            startActivity(intent)
            finish()
        }
    }
    private fun compareEmail(email: TextInputEditText) {
        if (email.text.toString().isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            return
        }
        firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }

        }
    }
}