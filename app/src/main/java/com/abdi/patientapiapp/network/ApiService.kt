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
}