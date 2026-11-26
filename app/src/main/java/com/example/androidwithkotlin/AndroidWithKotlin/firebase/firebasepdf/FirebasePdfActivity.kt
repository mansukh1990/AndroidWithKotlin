package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebasepdf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ActivityFirebasePdfBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebasePdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirebasePdfBinding
    private lateinit var storageRef: StorageReference
    private lateinit var databaseReference: DatabaseReference
    private var pdfFileUri: Uri? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfFileUri = uri
        val fileName = uri?.let { DocumentFile.fromSingleUri(this, it)?.name }
        binding.fileName.text = fileName.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        init()
        initListener()
    }

    private fun init() {
        binding = ActivityFirebasePdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance().reference.child("pdfs")
        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")
    }

    private fun initListener() {
        binding.selectPdfButton.setOnClickListener {
            launcher.launch("application/pdf")
        }

        binding.uploadBtn.setOnClickListener {
            if (pdfFileUri != null) {
                uploadPdfFileToFirebase()
            } else {
                Toast.makeText(this, "Please select a PDF file", Toast.LENGTH_SHORT).show()
            }
        }
        binding.showAllBtn.setOnClickListener {
            startActivity(Intent(this, AllPdfActivity::class.java))
        }
    }

    fun uploadPdfFileToFirebase() {
        val fileName = binding.fileName.text.toString()
        val fileRef = storageRef.child("${System.currentTimeMillis()}/$fileName")

        pdfFileUri?.let { uri ->
            fileRef.putFile(uri).addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val pdfFile = PdfFile(fileName, downloadUrl.toString())
                    databaseReference.push().key?.let { pushKey ->
                        databaseReference.child(pushKey).setValue(pdfFile)
                            .addOnSuccessListener {
                                pdfFileUri = null
                                binding.fileName.text =
                                    resources.getString(R.string.no_pdf_file_selected_yet)
                                Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT)
                                    .show()
                                if (binding.progressBar.isShown) {
                                    binding.progressBar.visibility = View.GONE
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                                if (binding.progressBar.isShown) {
                                    binding.progressBar.visibility = View.GONE
                                }
                            }

                    }
                }
            }.addOnProgressListener { uploadPdf ->
                val progress = (100 * uploadPdf.bytesTransferred) / uploadPdf.totalByteCount
                binding.progressBar.progress = progress.toInt()
                if (!binding.progressBar.isShown) {
                    binding.progressBar.visibility = View.VISIBLE
                }

            }.addOnFailureListener { e ->
                if (binding.progressBar.isShown) {
                    binding.progressBar.visibility = View.GONE
                }
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}