package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.androidwithkotlin.AndroidWithKotlin.firebase.signupLoginFirebaseRealtime.SignupToDoRequest
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null
    private lateinit var navController: NavController
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        navController = view.findNavController()
        loadingDialog = LoadingDialog(requireContext())
    }

    private fun registerEvents() {
        binding.textViewSignIn.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passEt.text.toString().trim()
            val verifyPass = binding.verifyPassEt.text.toString().trim()

            if (validateInputs(email, password, verifyPass)) {
                loadingDialog.show()
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: email.replace(".", "_")
                        database = FirebaseDatabase.getInstance()
                        reference = database!!.getReference("UsersToDo")

                        val signupRequest = SignupToDoRequest(
                            email = email, password = password
                        )
                        reference!!.child(userId).setValue(signupRequest).addOnCompleteListener { dbTask ->
                            loadingDialog.dismiss()
                            if (dbTask.isSuccessful) {
                                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_signUpFragment_to_signInFragment)
                            } else {
                                Toast.makeText(context, "Database Error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        loadingDialog.dismiss()
                        Toast.makeText(context, task.exception?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String, verifyPass: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(context, "Email is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(context, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        if (verifyPass.isEmpty()) {
            Toast.makeText(context, "Please confirm your password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != verifyPass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
