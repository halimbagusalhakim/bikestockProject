package com.example.bikestockproject.repositori

import com.example.bikestockproject.apiservice.RetrofitClient
import com.example.bikestockproject.modeldata.MerkModel

class MerkRepository {

    private val api = RetrofitClient.apiService

    suspend fun getAllMerk(token: String): Result<List<MerkModel>> {
        return try {
            val response = api.getAllMerk("Bearer $token")
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengambil data merk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMerk(token: String, merk: MerkModel): Result<MerkModel> {
        return try {
            val response = api.createMerk("Bearer $token", merk)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menambah merk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMerk(token: String, merk: MerkModel): Result<MerkModel> {
        return try {
            val response = api.updateMerk("Bearer $token", merk)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengubah merk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMerk(token: String, merkId: Int): Result<String> {
        return try {
            val response = api.deleteMerk("Bearer $token", merkId)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.message ?: "Berhasil menghapus merk")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus merk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}