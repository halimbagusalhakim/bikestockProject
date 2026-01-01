package com.example.bikestockproject.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikestockproject.modeldata.MerkModel
import com.example.bikestockproject.modeldata.ProdukModel
import com.example.bikestockproject.repositori.MerkRepository
import com.example.bikestockproject.repositori.ProdukRepository
import com.example.bikestockproject.uicontroller.route.DestinasiProdukEdit

import kotlinx.coroutines.launch

sealed class ProdukFormUiState {
    object Idle : ProdukFormUiState()
    object Loading : ProdukFormUiState()
    object Success : ProdukFormUiState()
    data class Error(val message: String) : ProdukFormUiState()
}

sealed class MerkDropdownUiState {
    object Loading : MerkDropdownUiState()
    data class Success(val merkList: List<MerkModel>) : MerkDropdownUiState()
    data class Error(val message: String) : MerkDropdownUiState()
}

data class ProdukFormState(
    val produkId: Int? = null,
    val merkId: Int = 0,
    val namaProduk: String = "",
    val deskripsi: String = "",
    val harga: String = "",
    val stok: String = "",
    val isMerkIdError: Boolean = false,
    val isNamaProdukError: Boolean = false,
    val isHargaError: Boolean = false,
    val isStokError: Boolean = false
)

class ProdukFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryProduk: ProdukRepository,
    private val repositoryMerk: MerkRepository
) : ViewModel() {

    var produkFormUiState: ProdukFormUiState by mutableStateOf(ProdukFormUiState.Idle)
        private set

    var merkDropdownUiState: MerkDropdownUiState by mutableStateOf(MerkDropdownUiState.Loading)
        private set

    var formState by mutableStateOf(ProdukFormState())
        private set

    private val produkId: Int? = savedStateHandle[DestinasiProdukEdit.produkIdArg]

    init {
        if (produkId != null) {
            formState = formState.copy(produkId = produkId)
        }
    }

    fun getMerkList(token: String) {
        viewModelScope.launch {
            merkDropdownUiState = MerkDropdownUiState.Loading

            repositoryMerk.getAllMerk(token)
                .onSuccess { merkList ->
                    merkDropdownUiState = MerkDropdownUiState.Success(merkList)
                }
                .onFailure { exception ->
                    merkDropdownUiState = MerkDropdownUiState.Error(
                        exception.message ?: "Gagal memuat data merk"
                    )
                }
        }
    }

    fun updateMerkId(merkId: Int) {
        formState = formState.copy(merkId = merkId, isMerkIdError = false)
    }

    fun updateNamaProduk(namaProduk: String) {
        formState = formState.copy(namaProduk = namaProduk, isNamaProdukError = false)
    }

    fun updateDeskripsi(deskripsi: String) {
        formState = formState.copy(deskripsi = deskripsi)
    }

    fun updateHarga(harga: String) {
        formState = formState.copy(harga = harga, isHargaError = false)
    }

    fun updateStok(stok: String) {
        formState = formState.copy(stok = stok, isStokError = false)
    }

    fun saveProduk(token: String) {
        if (!validateInput()) return

        viewModelScope.launch {
            produkFormUiState = ProdukFormUiState.Loading

            val produk = ProdukModel(
                produkId = formState.produkId,
                merkId = formState.merkId,
                namaProduk = formState.namaProduk,
                deskripsi = formState.deskripsi.ifBlank { null },
                harga = formState.harga.toInt(),
                stok = formState.stok.toInt()
            )

            val result = if (formState.produkId == null) {
                repositoryProduk.createProduk(token, produk)
            } else {
                repositoryProduk.updateProduk(token, produk)
            }

            result
                .onSuccess {
                    produkFormUiState = ProdukFormUiState.Success
                }
                .onFailure { exception ->
                    produkFormUiState = ProdukFormUiState.Error(
                        exception.message ?: "Gagal menyimpan produk"
                    )
                }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (formState.merkId == 0) {
            formState = formState.copy(isMerkIdError = true)
            isValid = false
        }

        if (formState.namaProduk.isBlank()) {
            formState = formState.copy(isNamaProdukError = true)
            isValid = false
        }

        if (formState.harga.isBlank() || formState.harga.toIntOrNull() == null) {
            formState = formState.copy(isHargaError = true)
            isValid = false
        }

        if (formState.stok.isBlank() || formState.stok.toIntOrNull() == null) {
            formState = formState.copy(isStokError = true)
            isValid = false
        }

        return isValid
    }

    fun resetState() {
        produkFormUiState = ProdukFormUiState.Idle
    }
}