package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebasepdf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidwithkotlin.databinding.ActivityAllPdfBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllPdfBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: PdfFilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference.child("pdfs")
        initRecyclerView()
        getAllPdfs()
    }

    private fun getAllPdfs() {
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val tempPdfFile = mutableListOf<PdfFile>()
                snapshot.children.forEach {
                    val pdfFile = it.getValue(PdfFile::class.java)
                    if (pdfFile != null) {
                        tempPdfFile.add(pdfFile)
                    }
                }
                if (tempPdfFile.isEmpty()) {
                    Toast.makeText(this@AllPdfActivity, "No files found", Toast.LENGTH_SHORT).show()

                }
                adapter.submitList(tempPdfFile)
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllPdfActivity, error.message, Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun initRecyclerView() {
        binding.pdfsRecyclerView.setHasFixedSize(true)
        binding.pdfsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = PdfFilesAdapter(onItemClick = { pdfFile ->
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("fileName", pdfFile.fileName)
            intent.putExtra("downloadUrl", pdfFile.downloadUrl)
            startActivity(intent)
        })
        binding.pdfsRecyclerView.adapter = adapter
    }
}