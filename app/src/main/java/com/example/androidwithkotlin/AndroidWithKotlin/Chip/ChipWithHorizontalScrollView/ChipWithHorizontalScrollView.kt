package com.example.androidwithkotlin.AndroidWithKotlin.Chip.ChipWithHorizontalScrollView

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidwithkotlin.databinding.ActivityChipWithHorizontalScrollViewBinding

class ChipWithHorizontalScrollView : AppCompatActivity() {

    private lateinit var binding: ActivityChipWithHorizontalScrollViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChipWithHorizontalScrollViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupChipS()
    }

    private fun setupChipS() {
        binding.chip3.setOnCloseIconClickListener {
            binding.chipGroup.removeView(it)
            Toast.makeText(this, "Remove one item", Toast.LENGTH_SHORT).show()
        }
        binding.chip4.setOnCloseIconClickListener {
            binding.chipGroup.removeView(it)
            Toast.makeText(this, "Remove one item", Toast.LENGTH_SHORT).show()
        }
        binding.chip5.setOnCloseIconClickListener {
            binding.chipGroup.removeView(it)
            Toast.makeText(this, "Remove one item", Toast.LENGTH_SHORT).show()
        }
        binding.chip6.setOnCloseIconClickListener {
            binding.chipGroup.removeView(it)
            Toast.makeText(this, "Remove one item", Toast.LENGTH_SHORT).show()
        }
    }
}