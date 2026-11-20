package com.example.androidwithkotlin.AndroidWithKotlin.firebase.signupLoginFirebaseRealtime

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivityProfileFirebaseRealtimeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFirebaseRealtimeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileFirebaseRealtimeBinding
    private var databaseReference: DatabaseReference? = null
    private var valueEventListener: ValueEventListener? = null
    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileFirebaseRealtimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataFromIntentOrSession()
        showUserDataFromRealtimeDatabase()

        binding.editButton.setOnClickListener {
            passUserData()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val sessionUsername = sharedPreferences.getString("username", null)
        if (!sessionUsername.isNullOrEmpty() && sessionUsername != currentUsername) {
            removeFirebaseListener()
            currentUsername = sessionUsername
            showUserDataFromRealtimeDatabase()
        }
    }

    private fun getDataFromIntentOrSession() {
        val intent = intent
        var nameUser = intent.getStringExtra("name")
        var emailUser = intent.getStringExtra("email")
        currentUsername = intent.getStringExtra("username")
        var passwordUser = intent.getStringExtra("password")

        if (currentUsername.isNullOrEmpty()) {
            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
            nameUser = sharedPreferences.getString("name", "")
            emailUser = sharedPreferences.getString("email", "")
            currentUsername = sharedPreferences.getString("username", "")
            passwordUser = sharedPreferences.getString("password", "")
        }

        binding.profileName.text = nameUser ?: ""
        binding.profileEmail.text = emailUser ?: ""
        binding.profileUsername.text = currentUsername ?: ""
        binding.profilePassword.text = passwordUser ?: ""
    }

    private fun showUserDataFromRealtimeDatabase() {
        if (!currentUsername.isNullOrEmpty()) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUsername!!)

            valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").getValue(String::class.java)
                        val email = snapshot.child("email").getValue(String::class.java)
                        val username = snapshot.child("username").getValue(String::class.java)
                        val password = snapshot.child("password").getValue(String::class.java)

                        binding.profileName.text = name ?: ""
                        binding.profileEmail.text = email ?: ""
                        binding.profileUsername.text = username ?: ""
                        binding.profilePassword.text = password ?: ""

                        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putString("name", name)
                            putString("email", email)
                            putString("username", username)
                            putString("password", password)
                            apply()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
            databaseReference?.addValueEventListener(valueEventListener as ValueEventListener)
        }
    }

    private fun removeFirebaseListener() {
        if (databaseReference != null && valueEventListener != null) {
            databaseReference?.removeEventListener(valueEventListener!!)
        }
    }

    fun passUserData() {
        val nameUser = binding.profileName.text.toString()
        val emailUser = binding.profileEmail.text.toString()
        val usernameUser = binding.profileUsername.text.toString()
        val passwordUser = binding.profilePassword.text.toString()

        val intent = Intent(this@ProfileFirebaseRealtimeActivity, EditProfileFirebaseRealtimeActivity::class.java).apply {
            putExtra("name", nameUser)
            putExtra("email", emailUser)
            putExtra("username", usernameUser)
            putExtra("password", passwordUser)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeFirebaseListener()
    }
}