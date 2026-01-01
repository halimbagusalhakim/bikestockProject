package com.example.bikestockproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.PenjualanDetailUiState
import com.example.bikestockproject.viewmodel.PenjualanDetailViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenjualanDetailScreen(
    navigateToPenjualanEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: PenjualanDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = "")

    LaunchedEffect(Unit) {
        token?.let { viewModel.getPenjualanDetail(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Penjualan") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = viewModel.penjualanDetailUiState) {
            is PenjualanDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PenjualanDetailUiState.Success -> {
                val penjualan = state.penjualan
                val formatRupiah = remember {
                    NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                        maximumFractionDigits = 0
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Informasi Penjualan Card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Informasi Penjualan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow(label = "ID", value = penjualan.penjualanId.toString())
                            DetailRow(label = "Nama Pembeli", value = penjualan.namaPembeli)
                            DetailRow(label = "Produk", value = penjualan.namaProduk ?: "-")
                            DetailRow(label = "Jumlah", value = "${penjualan.jumlah} Unit")
                            DetailRow(label = "Total Harga", value = formatRupiah.format(penjualan.totalHarga))
                            DetailRow(label = "Tanggal", value = penjualan.tanggalPenjualan ?: "-")
                            DetailRow(label = "Kasir", value = penjualan.username ?: "-")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button
                    Button(
                        onClick = { navigateToPenjualanEdit(penjualan.penjualanId!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Penjualan")
                    }
                }
            }
            is PenjualanDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }
        }
    }
}