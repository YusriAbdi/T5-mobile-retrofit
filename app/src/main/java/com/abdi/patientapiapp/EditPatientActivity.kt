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

class EditPatientActivity : AppCompatActivity() {

    private lateinit var etNama: TextInputEditText
    private lateinit var etJK: TextInputEditText
    private lateinit var etTanggalLahir: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etNoHp: TextInputEditText
    private lateinit var btnUpdate: Button
    private lateinit var progressBar: ProgressBar

    private var patientId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_patient)

        etNama = findViewById(R.id.etNama)
        etJK = findViewById(R.id.etJK)
        etTanggalLahir = findViewById(R.id.etTanggalLahir)
        etAlamat = findViewById(R.id.etAlamat)
        etNoHp = findViewById(R.id.etNoHp)
        btnUpdate = findViewById(R.id.btnUpdate)
        progressBar = findViewById(R.id.progressBar)

        patientId = intent.getIntExtra("PATIENT_ID", 0)
        etNama.setText(intent.getStringExtra("PATIENT_NAMA"))
        etJK.setText(intent.getStringExtra("PATIENT_JK"))
        etTanggalLahir.setText(intent.getStringExtra("PATIENT_TGL_LAHIR"))
        etAlamat.setText(intent.getStringExtra("PATIENT_ALAMAT"))
        etNoHp.setText(intent.getStringExtra("PATIENT_HP"))

        if (patientId == 0) {
            Toast.makeText(this, "ID Pasien tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnUpdate.setOnClickListener {
            updatePatient()
        }
    }

    private fun updatePatient() {
        val nama = etNama.text.toString().trim()
        val jk = etJK.text.toString().trim()
        val tglLahir = etTanggalLahir.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val noHp = etNoHp.text.toString().trim()

        if (nama.isEmpty() || jk.isEmpty() || tglLahir.isEmpty() || alamat.isEmpty() || noHp.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val patient = Patient(
            id = patientId,
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
                val response = RetrofitClient.apiService.updatePatient("Bearer $token", patientId, patient)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        Toast.makeText(this@EditPatientActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditPatientActivity, apiResponse?.message ?: "Gagal update", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        val json = JSONObject(errorBody ?: "")
                        val mainMsg = json.optString("message", "Gagal update")
                        val errors = json.optJSONObject("errors")
                        if (errors != null) {
                            val firstKey = errors.keys().next()
                            val detail = errors.getJSONArray(firstKey).getString(0)
                            "$mainMsg: $detail"
                        } else {
                            mainMsg
                        }
                    } catch (e: Exception) {
                        "Gagal update: ${response.code()}"
                    }
                    Toast.makeText(this@EditPatientActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditPatientActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnUpdate.isEnabled = !isLoading
    }
}
