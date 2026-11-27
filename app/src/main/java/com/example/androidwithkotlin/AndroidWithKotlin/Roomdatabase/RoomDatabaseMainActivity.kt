package com.example.androidwithkotlin.AndroidWithKotlin.Roomdatabase

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidwithkotlin.R
import com.example.androidwithkotlin.databinding.ActivityRoomDatabaseMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RoomDatabaseMainActivity : AppCompatActivity(),
    AddEditPersonFragment.AddEditPersonListener,
    PersonDetailsAdapter.PersonDetailsClickListener {

    private lateinit var binding: ActivityRoomDatabaseMainBinding
    private var dao: PersonDao? = null
    private lateinit var adapter: PersonDetailsAdapter
    private lateinit var searchQueryLiveData: MutableLiveData<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomDatabaseMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        attachUiListeners()
        subscribeDataStream()

    }

    private fun subscribeDataStream() {
        searchQueryLiveData.observe(this) { query ->
            lifecycleScope.launch {
                adapter.submitList(dao?.getSearchedPersons(query)?.first())
            }

        }
        lifecycleScope.launch {
            dao?.getAllPersons()?.collect {
                adapter.submitList(it)
//                binding.searchcView.setQuery("",false)
//                binding.searchcView.clearFocus()
                lifecycleScope.launch {
                    adapter.submitList(
                        dao?.getSearchedPersons(searchQueryLiveData.value ?: "")?.first()
                    )
                }
            }
        }
    }

    private fun init() {
        dao = PesonDatabase.getDatabase(this).personDao()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PersonDetailsAdapter(this)
        binding.recyclerView.adapter = adapter
        searchQueryLiveData = MutableLiveData("")

        val searchEditText =
            binding.searchcView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)
        searchEditText.setHintTextColor(Color.parseColor("#A0A0A0"))
    }

    private fun attachUiListeners() {
        binding.floatingActionButton.setOnClickListener {
            showBottomSheet()
        }
        binding.searchcView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null)
                    onQueryChanged(newText)
                return true
            }
        })

    }

    private fun onQueryChanged(query: String) {
        searchQueryLiveData.postValue(query)
//        lifecycleScope.launch {
//            adapter.submitList(dao?.getSearchedPersons(query)?.first())
//        }
    }

    private fun showBottomSheet(person: Person? = null) {
        val bottomSheet = AddEditPersonFragment.newInstance(person)
        bottomSheet.show(supportFragmentManager, AddEditPersonFragment.TAG)
    }

    override fun onSaveButtonClicked(isUpdate: Boolean, person: Person) {
        lifecycleScope.launch(Dispatchers.IO) {

            if (isUpdate)
                dao?.updatePerson(person = person)
            else
                dao?.savePerson(person)
        }
    }

    override fun onEditPersonClicked(person: Person) {
        showBottomSheet(person)
    }

    override fun onDeletePersonClicked(person: Person) {
        lifecycleScope.launch(Dispatchers.IO) {
            // dao?.deletePerson(person)
            dao?.deletePersonById(person.pId)
        }
    }
}
