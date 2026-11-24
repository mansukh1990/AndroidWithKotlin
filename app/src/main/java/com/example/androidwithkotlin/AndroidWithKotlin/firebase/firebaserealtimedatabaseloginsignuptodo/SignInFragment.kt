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
import com.example.androidwithkotlin.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
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
        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passEt.text.toString().trim()

            if (validateInputs(email, password)) {
                loadingDialog.show()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val userRef = FirebaseDatabase.getInstance().getReference("UsersToDo").child(currentUser.uid)
                            val userMap = SignupToDoRequest(email = email, password = password)
                            userRef.setValue(userMap).addOnCompleteListener { dbTask ->
                                loadingDialog.dismiss()
                                Toast.makeText(context, "Signed In Successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_signInFragment_to_homeFragment2)
                            }
                        } else {
                            loadingDialog.dismiss()
                            Toast.makeText(context, "Signed In Successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_signInFragment_to_homeFragment2)
                        }
                    } else {
                        loadingDialog.dismiss()
                        Toast.makeText(context, task.exception?.message ?: "sign in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
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
        return true
    }

}
