package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebasepdf

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidwithkotlin.databinding.ActivityPdfViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PdfViewActivity : AppCompatActivity(), DownloadProgressUpdater.DownloadProgressListener {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var downloadManager: DownloadManager
    private var downloadProgressUpdater: DownloadProgressUpdater? = null
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        snackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)


        val fileName = intent.getStringExtra("fileName")
        val downloadUrl = intent.getStringExtra("downloadUrl")

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        if (!downloadUrl.isNullOrEmpty()) {
            val localFile = File(cacheDir, fileName ?: "temp.pdf")
            try {
                FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl)
                    .getFile(localFile)
                    .addOnSuccessListener {
                        binding.progressBar.visibility = View.GONE
                        binding.pdfView.fromFile(localFile)
                            .onRender { pages ->
                                if (pages >= 1) {
                                    binding.progressBar.visibility = View.GONE
                                }
                            }
                            .load()
                    }
                    .addOnFailureListener { e ->
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Failed to load PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Invalid download URL", Toast.LENGTH_SHORT).show()
        }

        binding.floatingActionButton.setOnClickListener {
            downloadPdf(fileName, downloadUrl)
        }

    }

    fun downloadPdf(fileName: String?, downloadUrl: String?) {

        try {

            val downloadUri = Uri.parse(downloadUrl)
            val request = DownloadManager.Request(downloadUri)

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setMimeType("application/pdf")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    android.os.Environment.DIRECTORY_DOWNLOADS,
                    File.separator + fileName
                )
            val downloadId = downloadManager.enqueue(request)
            downloadProgressUpdater = DownloadProgressUpdater(downloadManager, downloadId, this)
            binding.progressBar.visibility = View.VISIBLE


            lifecycleScope.launch(Dispatchers.IO) {
                downloadProgressUpdater?.run()
            }
            snackbar.show()


        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun updateProgress(progress: Long) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (progress) {
                DOWNLOAD_SUCCESS -> {
                    snackbar.setText("Downloading ...... $progress%")
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PdfViewActivity, " Downloaded Successfully !!", Toast.LENGTH_SHORT).show()
                    snackbar.dismiss()
                }

                DOWNLOAD_FAILED -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PdfViewActivity, " Downloaded Failed !!", Toast.LENGTH_SHORT).show()
                    snackbar.dismiss()
                }

                else -> {
                    binding.progressBar.progress = progress.toInt()
                    snackbar.setText("Downloading ...... $progress%")
                }
            }
        }
    }


}