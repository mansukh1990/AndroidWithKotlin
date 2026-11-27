package com.example.androidwithkotlin.AndroidWithKotlin.Roomdatabase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidwithkotlin.databinding.FragmentAddEditPersonBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddEditPersonFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddEditPersonBinding
    private var listener: AddEditPersonListener? = null
    private var personToEdit: Person? = null

    companion object {
        const val TAG = "AddEditPersonFragment"

        @JvmStatic
        fun newInstance(person: Person?) = AddEditPersonFragment().apply {
            arguments = Bundle().apply {
                putParcelable("person", person)
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? AddEditPersonListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditPersonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            personToEdit = arguments?.getParcelable("person", Person::class.java)
        }

        setExistingDataOnUi()

        attachUiListeners()
    }

    private fun setExistingDataOnUi() {
        personToEdit?.let {
            binding.personNameEt.setText(it.name)
            binding.personAgeEt.setText(it.age.toString())
            binding.personCityEt.setText(it.city)
            binding.saveBtn.text = "Update"
        }
    }

    private fun attachUiListeners() {
        binding.saveBtn.setOnClickListener {
            val name = binding.personNameEt.text.toString()
            val age = binding.personAgeEt.text.toString()
            val city = binding.personCityEt.text.toString()

            if (name.isNotEmpty() && age.isNotEmpty() && city.isNotEmpty()) {
                val id = personToEdit?.pId ?: 0
                val person = Person(id, name, age.toInt(), city)
                listener?.onSaveButtonClicked(false, person)
            }
            dismiss()
        }
    }

    interface AddEditPersonListener {
        fun onSaveButtonClicked(isUpdate: Boolean, person: Person)
    }
}
