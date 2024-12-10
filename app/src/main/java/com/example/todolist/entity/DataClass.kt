package com.example.todolist.entity

data class DataClass(
    var key: String? = null, // Tidak perlu disimpan di database
    var dataId: String? = null,
    var dataTitle: String? = null,
    var dataDesc: String? = null,
    var dataPriority: String? = null
) {
    // Konstruktor kosong untuk Firebase
    constructor() : this(null, null, null, null, null)
}
