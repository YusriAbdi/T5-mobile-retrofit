package com.abdi.patientapiapp.network

import com.abdi.patientapiapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("pasien")
    suspend fun getPatients(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Patient>>>

    @POST("pasien")
    suspend fun createPatient(
        @Header("Authorization") token: String,
        @Body patient: Patient
    ): Response<ApiResponse<Patient>>

    @PUT("pasien/{id}")
    suspend fun updatePatient(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body patient: Patient
    ): Response<ApiResponse<Patient>>

    @DELETE("pasien/{id}")
    suspend fun deletePatient(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}