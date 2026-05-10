package com.abdi.patientapiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abdi.patientapiapp.R
import com.abdi.patientapiapp.model.Patient

class PatientAdapter(
    private val onEditClick: (Patient) -> Unit,
    private val onDeleteClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    private val patients = mutableListOf<Patient>()

    fun setData(newPatients: List<Patient>) {
        patients.clear()
        patients.addAll(newPatients)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(patients[position])
    }

    override fun getItemCount(): Int = patients.size

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        private val tvDetail: TextView = itemView.findViewById(R.id.tvDetail)
        private val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(patient: Patient) {
            tvNama.text = patient.nama
            tvDetail.text = "${patient.jenis_kelamin} - ${patient.no_telepon}"
            tvAlamat.text = patient.alamat

            btnEdit.setOnClickListener { onEditClick(patient) }
            btnDelete.setOnClickListener { onDeleteClick(patient) }
        }
    }
}