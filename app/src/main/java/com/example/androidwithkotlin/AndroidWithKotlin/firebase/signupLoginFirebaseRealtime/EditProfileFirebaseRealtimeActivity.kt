package com.example.androidwithkotlin.AndroidWithKotlin.firebase.signupLoginFirebaseRealtime

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivityEditProfileFirebaseRealtimeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class EditProfileFirebaseRealtimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileFirebaseRealtimeBinding
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null
    private var usernameUser: String? = null
    private var nameUser: String? = null
    private var emailUser: String? = null
    private var passwordUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileFirebaseRealtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        reference = database!!.getReference("Users")

        getDataFromIntent()

        binding.saveButton.setOnClickListener {
            saveData()
        }
    }

    fun isNameChanged(): Boolean {
        val newName = binding.editName.text.toString().trim()
        if (nameUser != newName) {
            usernameUser?.let { reference?.child(it)?.child("name")?.setValue(newName) }
            nameUser = newName
            return true
        }
        return false
    }

    fun isEmailChanged(): Boolean {
        val newEmail = binding.editEmail.text.toString().trim()
        if (emailUser != newEmail) {
            usernameUser?.let { reference?.child(it)?.child("email")?.setValue(newEmail) }
            emailUser = newEmail
            return true
        }
        return false
    }

    fun isPasswordChanged(): Boolean {
        val newPassword = binding.editPassword.text.toString().trim()
        if (passwordUser != newPassword) {
            usernameUser?.let { reference?.child(it)?.child("password")?.setValue(newPassword) }
            passwordUser = newPassword
            return true
        }
        return false
    }

    fun isUsernameChanged(): Boolean {
        val newUsername = binding.editUsername.text.toString().trim()
        if (!usernameUser.isNullOrEmpty() && usernameUser != newUsername) {
            reference?.child(usernameUser!!)?.child("username")?.setValue(newUsername)
            usernameUser = newUsername
            return true
        }
        return false
    }

    private fun getDataFromIntent() {
        val intent = getIntent()

        nameUser = intent.getStringExtra("name")
        emailUser = intent.getStringExtra("email")
        usernameUser = intent.getStringExtra("username")
        passwordUser = intent.getStringExtra("password")

        if (nameUser.isNullOrEmpty() || emailUser.isNullOrEmpty() || usernameUser.isNullOrEmpty() || passwordUser.isNullOrEmpty()) {
            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
            if (nameUser.isNullOrEmpty()) nameUser = sharedPreferences.getString("name", "")
            if (emailUser.isNullOrEmpty()) emailUser = sharedPreferences.getString("email", "")
            if (usernameUser.isNullOrEmpty()) usernameUser = sharedPreferences.getString("username", "")
            if (passwordUser.isNullOrEmpty()) passwordUser = sharedPreferences.getString("password", "")
        }

        binding.editName.setText(nameUser)
        binding.editUsername.setText(usernameUser)
        binding.editEmail.setText(emailUser)
        binding.editPassword.setText(passwordUser)
    }

    private fun saveData() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val username = binding.editUsername.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val nameChanged = isNameChanged()
        val emailChanged = isEmailChanged()
        val passwordChanged = isPasswordChanged()
        val usernameChanged = isUsernameChanged()

        if (nameChanged || emailChanged || passwordChanged || usernameChanged) {
            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putString("name", name)
                putString("email", email)
                putString("username", username)
                putString("password", password)
                apply()
            }
            Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "No Any Changes Found", Toast.LENGTH_SHORT).show()
        }
    }
}