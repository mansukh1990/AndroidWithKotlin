package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)


        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userRef = FirebaseDatabase.getInstance().getReference("UsersToDo").child(currentUser.uid)
                    userRef.get().addOnCompleteListener { task ->
                        if (isAdded) {
                            if (task.isSuccessful && task.result.exists()) {
                                navController.navigate(R.id.action_splashFragment_to_homeFragment2)
                            } else {
                                navController.navigate(R.id.action_splashFragment_to_signInFragment)
                            }
                        }
                    }
                } else {
                    navController.navigate(R.id.action_splashFragment_to_signInFragment)
                }
            }
        }, 3000)
    }
}