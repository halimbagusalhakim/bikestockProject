package com.example.bikestockproject.modeldata



import com.google.gson.annotations.SerializedName

/**
 * Model data untuk Produk Sepeda
 */
data class ProdukModel(
    @SerializedName("produk_id")
    val produkId: Int? = null,

    @SerializedName("merk_id")
    val merkId: Int,

    @SerializedName("nama_merk")
    val namaMerk: String? = null,

    @SerializedName("nama_produk")
    val namaProduk: String,

    @SerializedName("deskripsi")
    val deskripsi: String? = null,

    @SerializedName("harga")
    val harga: Int,

    @SerializedName("stok")
    val stok: Int,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)

/**
 * Response untuk list produk
 */
data class ProdukListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<ProdukModel>? = null
)

/**
 * Response untuk single produk
 */
data class ProdukResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ProdukModel? = null
)