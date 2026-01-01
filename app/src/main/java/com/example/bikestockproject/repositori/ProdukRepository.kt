package com.example.bikestockproject.repositori

import com.example.bikestockproject.apiservice.RetrofitClient
import com.example.bikestockproject.modeldata.ProdukModel


class ProdukRepository {

    private val api = RetrofitClient.apiService

    suspend fun getAllProduk(token: String): Result<List<ProdukModel>> {
        return try {
            val response = api.getAllProduk("Bearer $token")
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengambil data produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetailProduk(token: String, produkId: Int): Result<ProdukModel> {
        return try {
            val response = api.getDetailProduk("Bearer $token", produkId)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengambil detail produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduk(token: String, produk: ProdukModel): Result<ProdukModel> {
        return try {
            val response = api.createProduk("Bearer $token", produk)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menambah produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduk(token: String, produk: ProdukModel): Result<ProdukModel> {
        return try {
            val response = api.updateProduk("Bearer $token", produk)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengubah produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStok(token: String, produkId: Int, stok: Int): Result<ProdukModel> {
        return try {
            val request = mapOf(
                "produk_id" to produkId,
                "stok" to stok
            )
            val response = api.updateStok("Bearer $token", request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal mengubah stok"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduk(token: String, produkId: Int): Result<String> {
        return try {
            val response = api.deleteProduk("Bearer $token", produkId)
            if (response.isSuccessful && response.body()?.status == "success") {
                Result.success(response.body()?.message ?: "Berhasil menghapus produk")
            } else {
                Result.failure(Exception(response.body()?.message ?: "Gagal menghapus produk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}