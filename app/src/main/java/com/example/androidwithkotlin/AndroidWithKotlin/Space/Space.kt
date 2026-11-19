package com.example.androidwithkotlin.AndroidWithKotlin.Space

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivitySpaceBinding

class Space : AppCompatActivity() {

    private lateinit var binding: ActivitySpaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpaceBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}