package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebasepdf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidwithkotlin.databinding.ItemPdfBinding

class PdfFilesAdapter(
    private val onItemClick: ((PdfFile) -> Unit)? = null
) : ListAdapter<PdfFile, PdfFilesAdapter.PdfFilesViewHolder>(PdfDiffCallBack()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PdfFilesViewHolder {
        val binding = ItemPdfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfFilesViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: PdfFilesViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    class PdfFilesViewHolder(private val binding: ItemPdfBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pdfFile: PdfFile) {
            binding.fileName.text = pdfFile.fileName
        }
    }

    class PdfDiffCallBack : DiffUtil.ItemCallback<PdfFile>() {
        override fun areItemsTheSame(
            oldItem: PdfFile,
            newItem: PdfFile
        ): Boolean = oldItem.downloadUrl == newItem.downloadUrl

        override fun areContentsTheSame(
            oldItem: PdfFile,
            newItem: PdfFile
        ): Boolean = oldItem == newItem

    }

}