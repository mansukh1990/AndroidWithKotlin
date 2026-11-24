package com.example.androidwithkotlin.AndroidWithKotlin.firebase.firebaserealtimedatabaseloginsignuptodo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.FragmentHome3Binding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList
import java.util.Locale

class HomeFragment : Fragment(), ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var binding: FragmentHome3Binding
    private lateinit var navController: NavController
    private lateinit var databaseReference: DatabaseReference
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var dataList: ArrayList<ToDoClass>
    private lateinit var adapter: ToDoAdapter
    private var eventListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHome3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        loadingDialog = LoadingDialog(requireContext())

        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.search.clearFocus()

        dataList = ArrayList()
        adapter = ToDoAdapter(requireContext(), dataList) { selectedItem ->
            val bundle = Bundle().apply {
                putString("Title", selectedItem.dataTitle)
                putString("Desc", selectedItem.dataDesc)
                putString("Priority", selectedItem.dataPriority)
                putString("Image", selectedItem.dataImage)
                putString("Key", selectedItem.key)
            }
            navController.navigate(R.id.action_homeFragment2_to_toDoDetailsFragment, bundle)
        }
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Todo List")

        loadingDialog.show()

        eventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    try {
                        var toDoData = itemSnapshot.getValue(ToDoClass::class.java)
                        if (toDoData == null || (toDoData.dataTitle == null && toDoData.dataDesc == null)) {
                            val title = itemSnapshot.child("dataTitle").value?.toString()
                                ?: itemSnapshot.child("title").value?.toString()
                            val desc = itemSnapshot.child("dataDesc").value?.toString()
                                ?: itemSnapshot.child("desc").value?.toString()
                            val priority = itemSnapshot.child("dataPriority").value?.toString()
                                ?: itemSnapshot.child("priority").value?.toString()
                            val image = itemSnapshot.child("dataImage").value?.toString()
                                ?: itemSnapshot.child("image").value?.toString()

                            if (title != null || desc != null) {
                                toDoData = ToDoClass(title, desc, priority, image, itemSnapshot.key)
                            }
                        } else {
                            toDoData.key = itemSnapshot.key
                        }

                        if (toDoData != null && (toDoData.dataTitle != null || toDoData.dataDesc != null)) {
                            dataList.add(0, toDoData)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.updateDataList(ArrayList(dataList))
                checkEmptyState(dataList.isEmpty())
                loadingDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                loadingDialog.dismiss()
            }
        })

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_homeFragment2_to_toDoDIalogFragment)
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }

    override fun onDeleteClicked(toDoData: ToDoClass) {
        val taskKey = toDoData.key ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                deleteTaskFromFirebase(toDoData, taskKey)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteTaskFromFirebase(toDoData: ToDoClass, taskKey: String) {
        databaseReference.child(taskKey).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (!toDoData.dataImage.isNullOrEmpty()) {
                    try {
                        FirebaseStorage.getInstance().getReferenceFromUrl(toDoData.dataImage!!).delete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                Toast.makeText(requireContext(), "Task Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), task.exception?.message ?: "Delete failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditClicked(toDoData: ToDoClass) {
        val bundle = Bundle().apply {
            putString("Title", toDoData.dataTitle)
            putString("Desc", toDoData.dataDesc)
            putString("Priority", toDoData.dataPriority)
            putString("Image", toDoData.dataImage)
            putString("Key", toDoData.key)
        }
        navController.navigate(R.id.action_homeFragment2_to_toDoDIalogFragment, bundle)
    }

    private fun searchList(text: String) {
        if (text.isEmpty()) {
            adapter.updateDataList(ArrayList(dataList))
            checkEmptyState(dataList.isEmpty())
            return
        }
        val searchList = ArrayList<ToDoClass>()
        for (item in dataList) {
            if (item.dataTitle?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true) {
                searchList.add(item)
            }
        }
        adapter.searchDataList(searchList)
        checkEmptyState(searchList.isEmpty())
    }

    private fun checkEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        eventListener?.let {
            databaseReference.removeEventListener(it)
        }
    }
}