package com.example.bikestockproject.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikestockproject.modeldata.ProdukModel
import com.example.bikestockproject.repositori.ProdukRepository

import kotlinx.coroutines.launch

sealed class ProdukListUiState {
    object Loading : ProdukListUiState()
    data class Success(val produkList: List<ProdukModel>) : ProdukListUiState()
    data class Error(val message: String) : ProdukListUiState()
}

sealed class DeleteProdukUiState {
    object Idle : DeleteProdukUiState()
    object Loading : DeleteProdukUiState()
    object Success : DeleteProdukUiState()
    data class Error(val message: String) : DeleteProdukUiState()
}

class ProdukListViewModel(
    private val repositoryProduk: ProdukRepository
) : ViewModel() {

    var produkListUiState: ProdukListUiState by mutableStateOf(ProdukListUiState.Loading)
        private set

    var deleteProdukUiState: DeleteProdukUiState by mutableStateOf(DeleteProdukUiState.Idle)
        private set

    fun getProdukList(token: String) {
        viewModelScope.launch {
            produkListUiState = ProdukListUiState.Loading

            repositoryProduk.getAllProduk(token)
                .onSuccess { produkList ->
                    produkListUiState = ProdukListUiState.Success(produkList)
                }
                .onFailure { exception ->
                    produkListUiState = ProdukListUiState.Error(
                        exception.message ?: "Gagal memuat data produk"
                    )
                }
        }
    }

    fun deleteProduk(token: String, produkId: Int) {
        viewModelScope.launch {
            deleteProdukUiState = DeleteProdukUiState.Loading

            repositoryProduk.deleteProduk(token, produkId)
                .onSuccess {
                    deleteProdukUiState = DeleteProdukUiState.Success
                }
                .onFailure { exception ->
                    deleteProdukUiState = DeleteProdukUiState.Error(
                        exception.message ?: "Gagal menghapus produk"
                    )
                }
        }
    }

    fun resetDeleteState() {
        deleteProdukUiState = DeleteProdukUiState.Idle
    }
}