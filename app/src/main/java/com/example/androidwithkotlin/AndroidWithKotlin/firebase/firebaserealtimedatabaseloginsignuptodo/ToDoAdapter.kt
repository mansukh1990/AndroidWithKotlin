package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ItemToDoBinding

class ToDoAdapter(
    private val context: Context,
    private var toDoList: List<ToDoClass>,
    private val onItemClick: ((ToDoClass) -> Unit)? = null
) : RecyclerView.Adapter<ToDoAdapter.MyViewHolder>() {

    private var listener: ToDoAdapterClicksInterface? = null

    fun setListener(listener: ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    interface ToDoAdapterClicksInterface {
        fun onDeleteClicked(toDoData: ToDoClass)
        fun onEditClicked(toDoData: ToDoClass)
    }

    class MyViewHolder(val binding: ItemToDoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemToDoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = toDoList[position]
        holder.binding.apply {
            recTitle.text = item.dataTitle ?: ""
            recDesc.text = item.dataDesc ?: ""
            recPriority.text = item.dataPriority ?: ""

            if (!item.dataImage.isNullOrEmpty()) {
                Glide.with(context)
                    .load(item.dataImage)
                    .placeholder(R.drawable.ic_truck_upload)
                    .error(R.drawable.ic_truck_upload)
                    .into(recImage)
            } else {
                recImage.setImageResource(R.drawable.ic_truck_upload)
            }

            recEdit.setOnClickListener {
                listener?.onEditClicked(item)
            }

            recDelete.setOnClickListener {
                listener?.onDeleteClicked(item)
            }

            recCard.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    fun updateDataList(newList: List<ToDoClass>) {
        toDoList = newList
        notifyDataSetChanged()
    }

    fun searchDataList(searchList: List<ToDoClass>) {
        toDoList = searchList
        notifyDataSetChanged()
    }
}