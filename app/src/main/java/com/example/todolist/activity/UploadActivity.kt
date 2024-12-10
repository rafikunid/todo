package com.example.todolist.activity

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.databinding.ActivityUploadBinding
import com.example.todolist.entity.DataClass
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Calendar

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private var key: String? = null
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tangkap data dari Intent
        val intent = intent
        key = intent.getStringExtra("key")
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")
        val priority = intent.getStringExtra("priority")

        // Jika key tidak null, berarti ini update
        if (key != null) {
            isUpdate = true
            binding.buttonSave.text = "Update"
            binding.uploadActivity.setBackgroundResource(R.drawable.edittask_00000)
            binding.edittextUploadTitle.setText(title)
            binding.edittextUploadDesc.setText(desc)
            binding.edittextUploadPriority.setText(priority)
            binding.buttonSave.text = "Update Data" // Ubah teks tombol menjadi Update
        }


        // Set up edge-to-edge UI
        enableEdgeToEdge()
        binding.buttonSave.setOnClickListener {
            if (isUpdate) {
                updateData()
            } else {
                saveData()
            }
        }
    }

    private fun updateData() {
        val title = binding.edittextUploadTitle.text.toString().trim()
        val desc = binding.edittextUploadDesc.text.toString().trim()
        val priority = binding.edittextUploadPriority.text.toString().trim()

        if (title.isEmpty() || desc.isEmpty() || priority.isEmpty()) {
            Snackbar.make(binding.root, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        val dialog = AlertDialog.Builder(this@UploadActivity)
            .setCancelable(false)
            .setView(R.layout.progress_layout)
            .create()
        dialog.show()

        val databaseRef = FirebaseDatabase.getInstance().getReference("ToDos").child(key!!)

        val updatedData = mapOf(
            "dataTitle" to title,
            "dataDesc" to desc,
            "dataPriority" to priority
        )

        databaseRef.updateChildren(updatedData)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "Data updated successfully", Snackbar.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e("UploadActivity", "Failed to update data", task.exception)
                    Snackbar.make(binding.root, "Failed to update data: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Log.e("UploadActivity", "Error during Firebase operation", e)
                Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }

    }

    private fun saveData() {
        val title = binding.edittextUploadTitle.text.toString().trim()
        val desc = binding.edittextUploadDesc.text.toString().trim()
        val priority = binding.edittextUploadPriority.text.toString().trim()

        if (title.isEmpty() || desc.isEmpty() || priority.isEmpty()) {
            Snackbar.make(binding.root, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        val dialog = AlertDialog.Builder(this@UploadActivity)
            .setCancelable(false)
            .setView(R.layout.progress_layout)
            .create()
        dialog.show()

        val databaseRef = FirebaseDatabase.getInstance().getReference("ToDos").push()
        val currentData = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        // DataClass dengan key otomatis dan dataId
        val dataClass = DataClass(
            key = databaseRef.key, // Set key otomatis
            dataId = databaseRef.key, // Set dataId otomatis
            dataTitle = title,
            dataDesc = desc,
            dataPriority = priority
        )

        databaseRef.setValue(dataClass)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    Snackbar.make(binding.root, "Data saved successfully", Snackbar.LENGTH_SHORT).show()
                    finish() // Close the activity
                } else {
                    Log.e("UploadActivity", "Failed to save data", task.exception)
                    Snackbar.make(binding.root, "Failed to save data: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Log.e("UploadActivity", "Error during Firebase operation", e)
                Snackbar.make(binding.root, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

}