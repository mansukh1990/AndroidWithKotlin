package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.FragmentToDoDetailsBinding

class ToDoDetailsFragment : Fragment() {

    private lateinit var binding: FragmentToDoDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            val title = bundle.getString("Title")
            val desc = bundle.getString("Desc")
            val priority = bundle.getString("Priority")
            val imageUrl = bundle.getString("Image")

            binding.detailTitle.text = title ?: ""
            binding.detailDesc.text = desc ?: ""
            binding.detailPriority.text = priority ?: ""

            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_truck_upload)
                    .error(R.drawable.ic_truck_upload)
                    .into(binding.detailImage)
            } else {
                binding.detailImage.setImageResource(R.drawable.ic_truck_upload)
            }
        }
    }
}