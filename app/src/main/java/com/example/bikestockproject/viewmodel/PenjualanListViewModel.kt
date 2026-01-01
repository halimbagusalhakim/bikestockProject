package com.example.bikestockproject.viewmodel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bikestockproject.modeldata.PenjualanModel
import com.example.bikestockproject.repositori.PenjualanRepository

import kotlinx.coroutines.launch

sealed class PenjualanListUiState {
    object Loading : PenjualanListUiState()
    data class Success(val penjualanList: List<PenjualanModel>) : PenjualanListUiState()
    data class Error(val message: String) : PenjualanListUiState()
}

sealed class DeletePenjualanUiState {
    object Idle : DeletePenjualanUiState()
    object Loading : DeletePenjualanUiState()
    object Success : DeletePenjualanUiState()
    data class Error(val message: String) : DeletePenjualanUiState()
}

class PenjualanListViewModel(
    private val repositoryPenjualan: PenjualanRepository
) : ViewModel() {

    var penjualanListUiState: PenjualanListUiState by mutableStateOf(PenjualanListUiState.Loading)
        private set

    var deletePenjualanUiState: DeletePenjualanUiState by mutableStateOf(DeletePenjualanUiState.Idle)
        private set

    fun getPenjualanList(token: String) {
        viewModelScope.launch {
            penjualanListUiState = PenjualanListUiState.Loading

            repositoryPenjualan.getAllPenjualan(token)
                .onSuccess { penjualanList ->
                    penjualanListUiState = PenjualanListUiState.Success(penjualanList)
                }
                .onFailure { exception ->
                    penjualanListUiState = PenjualanListUiState.Error(
                        exception.message ?: "Gagal memuat data penjualan"
                    )
                }
        }
    }

    fun deletePenjualan(token: String, penjualanId: Int) {
        viewModelScope.launch {
            deletePenjualanUiState = DeletePenjualanUiState.Loading

            repositoryPenjualan.deletePenjualan(token, penjualanId)
                .onSuccess {
                    deletePenjualanUiState = DeletePenjualanUiState.Success
                }
                .onFailure { exception ->
                    deletePenjualanUiState = DeletePenjualanUiState.Error(
                        exception.message ?: "Gagal menghapus penjualan"
                    )
                }
        }
    }

    fun resetDeleteState() {
        deletePenjualanUiState = DeletePenjualanUiState.Idle
    }
}