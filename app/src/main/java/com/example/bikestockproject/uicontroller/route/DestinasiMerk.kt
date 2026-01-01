package com.example.bikestockproject.uicontroller.route

import com.example.bikestockproject.R

object DestinasiMerkList : DestinasiNavigasi {
    override val route = "list_merk"
    override val titleRes = R.string.list_merk
}

object DestinasiMerkEntry : DestinasiNavigasi {
    override val route = "entry_merk"
    override val titleRes = R.string.tambah_merk
}

object DestinasiMerkEdit : DestinasiNavigasi {
    override val route = "edit_merk"
    override val titleRes = R.string.edit_merk
    const val merkIdArg = "merkId"
    val routeWithArgs = "$route/{$merkIdArg}"
}