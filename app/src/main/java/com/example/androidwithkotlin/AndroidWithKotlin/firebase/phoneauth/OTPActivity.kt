package com.example.androidwithkotlin.AndroidWithKotlin.firebase.phoneauth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ActivityOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getDataFromIntent()
        init()
        addTextChangeListener()
        verifyOTP()
        startResendTimer()

        binding.resendTextView.setOnClickListener {
            if (binding.resendTextView.isEnabled) {
                resendOTP()
                startResendTimer()
            }
        }
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        binding.otpProgressBar.visibility = View.GONE
        binding.subtitleTextView.text = "Enter the 6-digit code sent to $phoneNumber"

        // Auto focus on first edit text and open soft keyboard on screen load
        binding.otpEditText1.postDelayed({
            binding.otpEditText1.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(binding.otpEditText1, InputMethodManager.SHOW_IMPLICIT)
        }, 300)
    }

    private fun startResendTimer() {
        clearOtpFields()
        binding.resendTextView.isEnabled = false
        binding.resendTextView.setTextColor(getColor(R.color.grey))

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.resendTextView.text = "Resend OTP in ${secondsRemaining}s"
            }

            override fun onFinish() {
                binding.resendTextView.isEnabled = true
                binding.resendTextView.text = "Resend OTP"
                binding.resendTextView.setTextColor(getColor(R.color.black))
            }
        }.start()
    }

    private fun clearOtpFields() {
        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")
        binding.otpEditText1.requestFocus()
    }

    private fun verifyOTP() {
        binding.verifyOTPBtn.setOnClickListener {
            val typedOTP =
                binding.otpEditText1.text.toString() +
                        binding.otpEditText2.text.toString() +
                        binding.otpEditText3.text.toString() +
                        binding.otpEditText4.text.toString() +
                        binding.otpEditText5.text.toString() +
                        binding.otpEditText6.text.toString()

            if (typedOTP.length == 6) {
                binding.otpProgressBar.visibility = View.VISIBLE
                binding.verifyOTPBtn.isEnabled = false

                val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(OTP, typedOTP)
                signInWithPhoneAuthCredential(credential)
            } else if (typedOTP.isEmpty()) {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter full 6-digit OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding.otpProgressBar.visibility = View.GONE
                binding.verifyOTPBtn.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    Log.e("PhoneAuth", "signInWithPhoneAuthCredential failed", task.exception)
                    Toast.makeText(
                        this,
                        "Verification failed: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun sendToMain() {
        startActivity(Intent(this, PhoneAuthMainActivity::class.java))
        finish()
    }

    private fun resendOTP() {
        binding.otpProgressBar.visibility = View.VISIBLE
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            binding.otpProgressBar.visibility = View.GONE
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.otpProgressBar.visibility = View.GONE
            Toast.makeText(
                this@OTPActivity,
                "Verification failed: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("PhoneAuth", "onVerificationFailed: ${e.message}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            binding.otpProgressBar.visibility = View.GONE
            OTP = verificationId
            resendToken = token
            Toast.makeText(this@OTPActivity, "OTP Resent successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTextChangeListener() {
        binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
        binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
        binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
        binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
        binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
        binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))
    }

    private fun getDataFromIntent() {
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = IntentCompat.getParcelableExtra(
            intent,
            "resendToken",
            PhoneAuthProvider.ForceResendingToken::class.java
        )!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            when (view.id) {
                R.id.otpEditText1 -> {
                    if (text.length == 1) {
                        binding.otpEditText2.requestFocus()
                    }
                }

                R.id.otpEditText2 -> {
                    if (text.length == 1) {
                        binding.otpEditText3.requestFocus()
                    } else if (text.isEmpty()) {
                        binding.otpEditText1.requestFocus()
                    }
                }

                R.id.otpEditText3 -> {
                    if (text.length == 1) {
                        binding.otpEditText4.requestFocus()
                    } else if (text.isEmpty()) {
                        binding.otpEditText2.requestFocus()
                    }
                }

                R.id.otpEditText4 -> {
                    if (text.length == 1) {
                        binding.otpEditText5.requestFocus()
                    } else if (text.isEmpty()) {
                        binding.otpEditText3.requestFocus()
                    }
                }

                R.id.otpEditText5 -> {
                    if (text.length == 1) {
                        binding.otpEditText6.requestFocus()
                    } else if (text.isEmpty()) {
                        binding.otpEditText4.requestFocus()
                    }
                }

                R.id.otpEditText6 -> {
                    if (text.isEmpty()) {
                        binding.otpEditText5.requestFocus()
                    }
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
}