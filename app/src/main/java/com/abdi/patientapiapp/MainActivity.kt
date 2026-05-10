package com.abdi.patientapiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdi.patientapiapp.adapter.PatientAdapter
import com.abdi.patientapiapp.model.Patient
import com.abdi.patientapiapp.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvPatients: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPatients = findViewById(R.id.rvPatients)
        progressBar = findViewById(R.id.progressBar)
        fabAdd = findViewById(R.id.fabAdd)

        adapter = PatientAdapter(
            onEditClick = { patient ->
                val intent = Intent(this, EditPatientActivity::class.java).apply {
                    putExtra("PATIENT_ID", patient.id)
                    putExtra("PATIENT_NAMA", patient.nama)
                    putExtra("PATIENT_JK", patient.jenis_kelamin)
                    putExtra("PATIENT_TGL_LAHIR", patient.tanggal_lahir)
                    putExtra("PATIENT_ALAMAT", patient.alamat)
                    putExtra("PATIENT_HP", patient.no_telepon)
                }
                startActivity(intent)
            },
            onDeleteClick = { patient ->
                showDeleteConfirmation(patient)
            }
        )
        rvPatients.layoutManager = LinearLayoutManager(this)
        rvPatients.adapter = adapter

        fabAdd.setOnClickListener {
            val intent = Intent(this, AddPatientActivity::class.java)
            startActivity(intent)
        }

        loadPatients()
    }

    override fun onResume() {
        super.onResume()
        loadPatients()
    }

    private fun getToken(): String? {
        val prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return prefs.getString("user_token", null)
    }

    private fun showDeleteConfirmation(patient: Patient) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus pasien ${patient.nama}?")
            .setPositiveButton("Hapus") { dialog, _ ->
                deletePatient(patient)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deletePatient(patient: Patient) {
        val id = patient.id ?: return
        val token = getToken() ?: return

        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.deletePatient("Bearer $token", id)
                if (response.isSuccessful) {
                    showMessage("Data berhasil dihapus")
                    loadPatients()
                } else {
                    showMessage("Gagal menghapus data: ${response.code()}")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun loadPatients() {
        val token = getToken()
        if (token == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.getPatients("Bearer $token")
                if (response.isSuccessful) {
                    val patients = response.body()?.data ?: emptyList()
                    adapter.setData(patients)
                } else {
                    showMessage("Gagal mengambil data: ${response.code()}")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
