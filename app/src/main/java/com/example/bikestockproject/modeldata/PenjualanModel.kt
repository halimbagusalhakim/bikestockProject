package com.example.bikestockproject.modeldata


import com.google.gson.annotations.SerializedName

/**
 * Model data untuk Penjualan
 */
data class PenjualanModel(
    @SerializedName("penjualan_id")
    val penjualanId: Int? = null,

    @SerializedName("produk_id")
    val produkId: Int,

    @SerializedName("nama_produk")
    val namaProduk: String? = null,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("nama_pembeli")
    val namaPembeli: String,

    @SerializedName("jumlah")
    val jumlah: Int,

    @SerializedName("total_harga")
    val totalHarga: Int,

    @SerializedName("tanggal_penjualan")
    val tanggalPenjualan: String? = null
)

/**
 * Response untuk list penjualan
 */
data class PenjualanListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<PenjualanModel>? = null
)

/**
 * Response untuk single penjualan
 */
data class PenjualanResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: PenjualanModel? = null
)