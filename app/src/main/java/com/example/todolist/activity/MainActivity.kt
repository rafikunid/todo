package com.example.todolist.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todolist.R
import com.example.todolist.adapter.MyAdapter
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.entity.DataClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    private lateinit var eventListener: ValueEventListener
    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Setup RecyclerView with GridLayoutManager
        val gridLayoutManager = GridLayoutManager(this@MainActivity, 1)
        binding.recycleView.layoutManager = gridLayoutManager

        // Initialize dialog for loading progress
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        // Initialize data list and adapter
        dataList = ArrayList()
        adapter = MyAdapter(this@MainActivity, dataList)
        binding.recycleView.adapter = adapter

        // Set up database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("ToDos")

        // Add event listener to read data from Firebase
        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                val tempList = ArrayList<DataClass>()
                for (dataSnapshot in snapshot.children) {
                    try {
                        val item = dataSnapshot.getValue(DataClass::class.java)
                        if (item != null) {
                            item.key = dataSnapshot.key // Simpan key utama
                            tempList.add(item)
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error parsing data: ${e.message}")
                    }
                }
                dataList.addAll(tempList)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read data: ${error.message}")
                dialog.dismiss() // Tutup dialog jika ada error
            }
        }
        databaseReference?.addValueEventListener(eventListener)

        // Set up FAB to navigate to UploadActivity
        binding.fab.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        // Set up search functionality
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
    }

    private fun searchList(text: String) {
        val searchList = dataList.filter {
            it.dataTitle?.contains(text, ignoreCase = true) == true
        }
        adapter.updateDataList(ArrayList(searchList))
    }
}
