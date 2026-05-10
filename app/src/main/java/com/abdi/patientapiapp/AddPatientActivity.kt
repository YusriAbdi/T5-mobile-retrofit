package com.abdi.patientapiapp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abdi.patientapiapp.model.Patient
import com.abdi.patientapiapp.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddPatientActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etJK: TextInputEditText
    private lateinit var etTanggalLahir: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etNoHp: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        etNama = findViewById(R.id.etNama)
        etJK = findViewById(R.id.etJK)
        etTanggalLahir = findViewById(R.id.etTanggalLahir)
        etAlamat = findViewById(R.id.etAlamat)
        etNoHp = findViewById(R.id.etNoHp)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        btnSave.setOnClickListener {
            savePatient()
        }
    }

    private fun savePatient() {
        val nama = etNama.text.toString().trim()
        val jk = etJK.text.toString().trim()
        val tglLahir = etTanggalLahir.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val noHp = etNoHp.text.toString().trim()

        if (nama.isEmpty() || jk.isEmpty() || tglLahir.isEmpty() || alamat.isEmpty() || noHp.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Gunakan no_telepon sesuai dengan model Patient dan kebutuhan API
        val patient = Patient(
            nama = nama,
            jenis_kelamin = jk,
            tanggal_lahir = tglLahir,
            alamat = alamat,
            no_telepon = noHp
        )

        val prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val token = prefs.getString("user_token", null)

        if (token == null) {
            Toast.makeText(this, "Sesi habis, silakan login kembali", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            showLoading(true)
            try {
                val response = RetrofitClient.apiService.createPatient("Bearer $token", patient)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        Toast.makeText(this@AddPatientActivity, "Pasien berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddPatientActivity, apiResponse?.message ?: "Gagal menambah pasien", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        val json = JSONObject(errorBody ?: "")
                        val mainMsg = json.optString("message", "Data tidak valid")
                        val errors = json.optJSONObject("errors")
                        if (errors != null) {
                            val firstKey = errors.keys().next()
                            val detail = errors.getJSONArray(firstKey).getString(0)
                            "$mainMsg: $detail"
                        } else {
                            mainMsg
                        }
                    } catch (e: Exception) {
                        "Gagal menambah: ${response.code()}"
                    }
                    Toast.makeText(this@AddPatientActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddPatientActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSave.isEnabled = !isLoading
    }
}
