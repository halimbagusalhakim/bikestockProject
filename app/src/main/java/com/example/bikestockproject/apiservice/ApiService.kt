package com.example.bikestockproject.apiservice

import com.example.bikestockproject.modeldata.BaseResponse
import com.example.bikestockproject.modeldata.LoginRequest
import com.example.bikestockproject.modeldata.LoginResponse
import com.example.bikestockproject.modeldata.MerkListResponse
import com.example.bikestockproject.modeldata.MerkModel
import com.example.bikestockproject.modeldata.MerkResponse
import com.example.bikestockproject.modeldata.PenjualanListResponse
import com.example.bikestockproject.modeldata.PenjualanModel
import com.example.bikestockproject.modeldata.PenjualanResponse
import com.example.bikestockproject.modeldata.ProdukListResponse
import com.example.bikestockproject.modeldata.ProdukModel
import com.example.bikestockproject.modeldata.ProdukResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    // ==================== AUTH ====================
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/logout.php")
    suspend fun logout(@Header("Authorization") token: String): Response<BaseResponse>

    // ==================== MERK ====================
    @GET("merk/read.php")
    suspend fun getAllMerk(@Header("Authorization") token: String): Response<MerkListResponse>

    @POST("merk/create.php")
    suspend fun createMerk(
        @Header("Authorization") token: String,
        @Body merk: MerkModel
    ): Response<MerkResponse>

    @PUT("merk/update.php")
    suspend fun updateMerk(
        @Header("Authorization") token: String,
        @Body merk: MerkModel
    ): Response<MerkResponse>

    @DELETE("merk/delete.php")
    suspend fun deleteMerk(
        @Header("Authorization") token: String,
        @Query("id") merkId: Int
    ): Response<BaseResponse>

    // ==================== PRODUK ====================
    @GET("produk/read.php")
    suspend fun getAllProduk(@Header("Authorization") token: String): Response<ProdukListResponse>

    @GET("produk/detail.php")
    suspend fun getDetailProduk(
        @Header("Authorization") token: String,
        @Query("id") produkId: Int
    ): Response<ProdukResponse>

    @POST("produk/create.php")
    suspend fun createProduk(
        @Header("Authorization") token: String,
        @Body produk: ProdukModel
    ): Response<ProdukResponse>

    @PUT("produk/update.php")
    suspend fun updateProduk(
        @Header("Authorization") token: String,
        @Body produk: ProdukModel
    ): Response<ProdukResponse>

    @PUT("produk/stock.php")
    suspend fun updateStok(
        @Header("Authorization") token: String,
        @Body request: Map<String, Int>
    ): Response<ProdukResponse>

    @DELETE("produk/delete.php")
    suspend fun deleteProduk(
        @Header("Authorization") token: String,
        @Query("id") produkId: Int
    ): Response<BaseResponse>


    // ==================== PENJUALAN ====================
    @GET("penjualan/read.php")
    suspend fun getAllPenjualan(@Header("Authorization") token: String): Response<PenjualanListResponse>

    @GET("penjualan/detail.php")
    suspend fun getDetailPenjualan(
        @Header("Authorization") token: String,
        @Query("id") penjualanId: Int
    ): Response<PenjualanResponse>

    @POST("penjualan/create.php")
    suspend fun createPenjualan(
        @Header("Authorization") token: String,
        @Body penjualan: PenjualanModel
    ): Response<PenjualanResponse>

    @PUT("penjualan/update.php")
    suspend fun updatePenjualan(
        @Header("Authorization") token: String,
        @Body penjualan: PenjualanModel
    ): Response<PenjualanResponse>

    @DELETE("penjualan/delete.php")
    suspend fun deletePenjualan(
        @Header("Authorization") token: String,
        @Query("id") penjualanId: Int
    ): Response<BaseResponse>
}