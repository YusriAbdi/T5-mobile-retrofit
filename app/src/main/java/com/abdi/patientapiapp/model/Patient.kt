package com.abdi.patientapiapp.model

data class Patient(
    val id: Int? = null,
    val nama: String,
    val jenis_kelamin: String,
    val tanggal_lahir: String,
    val alamat: String,
    val no_telepon: String,
    val created_at: String? = null,
    val updated_at: String? = null
)
