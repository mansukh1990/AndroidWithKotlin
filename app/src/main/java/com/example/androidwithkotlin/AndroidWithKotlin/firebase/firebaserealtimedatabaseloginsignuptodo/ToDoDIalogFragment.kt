package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.FragmentToDoDIalogBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ToDoDIalogFragment : Fragment() {

    private lateinit var binding: FragmentToDoDIalogBinding
    private lateinit var loadingDialog: LoadingDialog
    private var imageURL: String? = null
    var uri: Uri? = null
    private var key: String? = null
    private var oldImageUrl: String? = null

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            uri = data?.data
            binding.uploadImage.setImageURI(uri)
        } else {
            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoDIalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireContext())

        val bundle = arguments
        if (bundle != null && bundle.containsKey("Key")) {
            key = bundle.getString("Key")
            oldImageUrl = bundle.getString("Image")
            imageURL = oldImageUrl

            binding.uploadTitle.setText(bundle.getString("Title"))
            binding.uploadDesc.setText(bundle.getString("Desc"))
            binding.uploadPriority.setText(bundle.getString("Priority"))
            binding.saveButton.text = "UPDATE"

            if (!oldImageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(oldImageUrl)
                    .placeholder(R.drawable.ic_upload_image)
                    .error(R.drawable.ic_upload_image)
                    .into(binding.uploadImage)
            }
        }

        binding.uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        binding.saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val title = binding.uploadTitle.text.toString().trim()
        val desc = binding.uploadDesc.text.toString().trim()
        val priority = binding.uploadPriority.text.toString().trim()

        if (title.isEmpty() || desc.isEmpty() || priority.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (key == null && uri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show()

        if (uri != null) {
            val storageReference = FirebaseStorage.getInstance().reference
                .child("Task Images")
                .child(uri?.lastPathSegment ?: System.currentTimeMillis().toString())

            storageReference.putFile(uri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { urlImage ->
                        imageURL = urlImage.toString()
                        uploadData()
                    }.addOnFailureListener { e ->
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        } else {
            uploadData()
        }
    }

    private fun uploadData() {
        val title = binding.uploadTitle.text.toString().trim()
        val desc = binding.uploadDesc.text.toString().trim()
        val priority = binding.uploadPriority.text.toString().trim()

        val databaseRef = FirebaseDatabase.getInstance().getReference("Todo List")
        val targetKey = key ?: databaseRef.push().key ?: System.currentTimeMillis().toString()

        val toDoData = ToDoClass(title, desc, priority, imageURL, targetKey)

        databaseRef.child(targetKey)
            .setValue(toDoData)
            .addOnCompleteListener { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    val msg = if (key != null) "Task Updated Successfully" else "Task Saved Successfully"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}