package com.example.bikestockproject.repositori

import com.example.bikestockproject.apiservice.RetrofitClient
import com.example.bikestockproject.modeldata.LoginRequest
import com.example.bikestockproject.modeldata.LoginResponse


class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(username, password)
            val response = api.login(request)

            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Login gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(token: String): Result<String> {
        return try {
            val response = api.logout("Bearer $token")

            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.message ?: "Logout berhasil")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Logout gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}