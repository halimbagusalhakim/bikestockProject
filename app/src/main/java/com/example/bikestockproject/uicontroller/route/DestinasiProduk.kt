package com.example.bikestockproject.uicontroller.route

import com.example.bikestockproject.R


object DestinasiProdukList : DestinasiNavigasi {
    override val route = "list_produk"
    override val titleRes = R.string.list_produk
    const val merkIdArg = "merkId"
    const val merkNameArg = "merkName"
    val routeWithArgs = "$route?merkId={$merkIdArg}&merkName={$merkNameArg}"
}

object DestinasiProdukEntry : DestinasiNavigasi {
    override val route = "entry_produk"
    override val titleRes = R.string.tambah_produk
    const val merkIdArg = "merkId"
    const val merkNameArg = "merkName"
    // Route dengan pattern untuk menangkap argumen
    val routeWithArgs = "$route/{$merkIdArg}/{$merkNameArg}"
}

object DestinasiProdukDetail : DestinasiNavigasi {
    override val route = "detail_produk"
    override val titleRes = R.string.detail_produk
    const val produkIdArg = "produkId"
    val routeWithArgs = "$route/{$produkIdArg}"
}

object DestinasiProdukEdit : DestinasiNavigasi {
    override val route = "edit_produk"
    override val titleRes = R.string.edit_produk
    const val produkIdArg = "produkId"
    val routeWithArgs = "$route/{$produkIdArg}"
}

object DestinasiProdukStok : DestinasiNavigasi {
    override val route = "stok_produk"
    override val titleRes = R.string.update_stok
    const val produkIdArg = "produkId"
    val routeWithArgs = "$route/{$produkIdArg}"
}