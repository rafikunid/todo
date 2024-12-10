package com.example.todolist.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todolist.R
import com.example.todolist.activity.UploadActivity
import com.example.todolist.entity.DataClass
import com.google.firebase.database.FirebaseDatabase

class MyAdapter(
    private val context: Context,
    private var dataList: MutableList<DataClass> // Ganti List dengan MutableList agar bisa dimodifikasi
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Bind data ke item
        val currentItem = dataList[position]
        holder.recTitle.text = currentItem.dataTitle
        holder.recDesc.text = currentItem.dataDesc
        holder.recPriority.text = currentItem.dataPriority

        // Klik tombol hapus
        holder.deleteButton.setOnClickListener {
            deleteItem(position, currentItem)
        }

        // Tambahkan tindakan jika diperlukan untuk klik item card
        holder.recCard.setOnClickListener {
            val intent = Intent(context, UploadActivity::class.java).apply {
                putExtra("key", currentItem.key) // Kirim key
                putExtra("title", currentItem.dataTitle) // Kirim judul
                putExtra("desc", currentItem.dataDesc) // Kirim deskripsi
                putExtra("priority", currentItem.dataPriority) // Kirim prioritas
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    // Fungsi untuk memperbarui daftar pencarian
    fun searchDataList(searchList: List<DataClass>) {
        dataList = searchList.toMutableList()
        notifyDataSetChanged()
    }

    fun updateDataList(newDataList: List<DataClass>) {
        dataList.clear()
        dataList.addAll(newDataList)
        notifyDataSetChanged()
    }


    // Fungsi untuk menghapus item
    private fun deleteItem(position: Int, item: DataClass) {
        if (position >= 0 && position < dataList.size) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        } else {
            Log.e("MyAdapter", "Gagal menghapus item: Indeks $position tidak valid, ukuran daftar: ${dataList.size}")
            return
        }

        val key = item.key
        if (key.isNullOrEmpty()) {
            Log.e("MyAdapter", "Gagal menghapus item: key kosong atau null")
            return
        }

        val databaseReference = FirebaseDatabase.getInstance().getReference("ToDos")
        databaseReference.child(key)
            .removeValue()
            .addOnSuccessListener {
                Log.d("MyAdapter", "Item berhasil dihapus dari Firebase")
            }
            .addOnFailureListener { error ->
                Log.e("MyAdapter", "Gagal menghapus item dari Firebase: ${error.message}")
            }
    }

}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recTitle: TextView = itemView.findViewById(R.id.textviewRecTitle)
    var recDesc: TextView = itemView.findViewById(R.id.textviewRecDesc)
    var recPriority: TextView = itemView.findViewById(R.id.textviewRecPriority)
    var deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    var recCard: CardView = itemView.findViewById(R.id.cardview)
}