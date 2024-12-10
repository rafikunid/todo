package com.example.todolist.activity

data class User(
    val email: String = "",
    val password: String = "" // Hindari menyimpan password plaintext di produksi
)
