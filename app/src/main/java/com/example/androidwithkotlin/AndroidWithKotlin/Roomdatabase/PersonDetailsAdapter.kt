package com.example.androidwithkotlin.AndroidWithKotlin.Roomdatabase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidwithkotlin.databinding.SingleItemBinding

class PersonDetailsAdapter(
    private val listener: PersonDetailsClickListener
) : ListAdapter<Person, PersonDetailsAdapter.PersonViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonViewHolder {
        return PersonViewHolder(
            SingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: PersonViewHolder,
        position: Int
    ) {
        val person = getItem(position)
        holder.bind(person, listener)
    }

    class PersonViewHolder(private val binding: SingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person, listener: PersonDetailsClickListener) {
            binding.personNameTv.text = person.name
            binding.personAgeTv.text = person.age.toString()
            binding.personCityTv.text = person.city

            binding.editBtn.setOnClickListener {
                listener.onEditPersonClicked(person)
            }
            binding.deleteBtn.setOnClickListener {
                listener.onDeletePersonClicked(person)
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(
            oldItem: Person,
            newItem: Person
        ) = oldItem.pId == newItem.pId

        override fun areContentsTheSame(
            oldItem: Person,
            newItem: Person
        ) = oldItem == newItem
    }

    interface PersonDetailsClickListener {
        fun onEditPersonClicked(person: Person)
        fun onDeletePersonClicked(person: Person)
    }
}
