package com.example.androidwithkotlin.AndroidWithKotlin.firebase.phoneauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ActivityPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        binding.sendOTPBtn.setOnClickListener {
            phoneNumber = binding.phoneEditTextNumber.text.trim().toString()
            if (phoneNumber.isNotEmpty()) {
                if (phoneNumber.length == 10) {
                    phoneNumber = "+91$phoneNumber"
                    binding.phoneProgressBar.visibility = View.VISIBLE
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)

                } else {
                    Toast.makeText(this, "Please enter correct Phone number", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please enter Phone number", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        binding.phoneProgressBar.visibility = View.INVISIBLE
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    Log.d(
                        "FirebaseAuthInvalidCredentialsException",
                        "signInWithPhoneAuthCredential: ${task.exception.toString()}"
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                    // Update UI
                }
                binding.phoneProgressBar.visibility = View.INVISIBLE
            }
    }

    fun sendToMain() {
        startActivity(Intent(this, PhoneAuthMainActivity::class.java))
        finish()
    }

   private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Log.e(
                        "FirebaseAuthInvalidCredentialsException",
                        "onVerificationFailed:${e.message} "
                    )
                }

                is FirebaseTooManyRequestsException -> {
                    Log.e("FirebaseTooManyRequestsException", "onVerificationFailed: ${e.message}")
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    Log.e(
                        "FirebaseAuthMissingActivityForRecaptchaException",
                        "onVerificationFailed: ${e.message}",
                    )
                }
            }


        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            val intent = Intent(this@PhoneActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", phoneNumber)
            startActivity(intent)
            binding.phoneProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            sendToMain()
            finish()
        }
    }
}