package com.abdi.patientapiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abdi.patientapiapp.adapter.PatientAdapter
import com.abdi.patientapiapp.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvPatients: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPatients = findViewById(R.id.rvPatients)
        progressBar = findViewById(R.id.progressBar)

        adapter = PatientAdapter()
        rvPatients.layoutManager = LinearLayoutManager(this)
        rvPatients.adapter = adapter

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
