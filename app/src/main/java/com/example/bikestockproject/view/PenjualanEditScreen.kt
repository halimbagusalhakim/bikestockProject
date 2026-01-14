package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.PenjualanFormUiState
import com.example.bikestockproject.viewmodel.PenjualanFormViewModel
import com.example.bikestockproject.viewmodel.ProdukDropdownUiState
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenjualanEditScreen(
    navigateBack: () -> Unit,
    viewModel: PenjualanFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    var expandedProduk by remember { mutableStateOf(false) }

    val formatRupiah = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    LaunchedEffect(token) {
        token?.let {
            if (it.isNotEmpty()) {
                viewModel.loadPenjualanData(it)
                viewModel.getProdukList(it)
            }
        }
    }

    LaunchedEffect(viewModel.penjualanFormUiState) {
        when (val state = viewModel.penjualanFormUiState) {
            is PenjualanFormUiState.Success -> {
                Toast.makeText(context, "Penjualan berhasil diubah", Toast.LENGTH_SHORT).show()
                navigateBack()
            }
            is PenjualanFormUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Penjualan") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
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

                    // Nama Pembeli
                    OutlinedTextField(
                        value = viewModel.formState.namaPembeli,
                        onValueChange = { viewModel.updateNamaPembeli(it) },
                        label = { Text("Nama Pembeli") },
                        isError = viewModel.formState.isNamaPembeliError,
                        supportingText = {
                            if (viewModel.formState.isNamaPembeliError) {
                                Text("Nama pembeli tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dropdown Produk
                    ExposedDropdownMenuBox(
                        expanded = expandedProduk,
                        onExpandedChange = { expandedProduk = it }
                    ) {
                        OutlinedTextField(
                            value = when (val state = viewModel.produkDropdownUiState) {
                                is ProdukDropdownUiState.Success -> {
                                    state.produkList.find { it.produkId == viewModel.formState.produkId }?.namaProduk ?: ""
                                }
                                else -> ""
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Produk") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            isError = viewModel.formState.isProdukIdError,
                            supportingText = {
                                if (viewModel.formState.isProdukIdError) {
                                    Text("Produk harus dipilih")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedProduk,
                            onDismissRequest = { expandedProduk = false }
                        ) {
                            when (val state = viewModel.produkDropdownUiState) {
                                is ProdukDropdownUiState.Success -> {
                                    state.produkList.forEach { produk ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(produk.namaProduk)
                                                    Text(
                                                        text = "${formatRupiah.format(produk.harga)} • Stok: ${produk.stok}",
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                    )
                                                }
                                            },
                                            onClick = {
                                                viewModel.updateProdukId(produk.produkId!!, produk.harga)
                                                expandedProduk = false
                                            }
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Jumlah
                    OutlinedTextField(
                        value = viewModel.formState.jumlah,
                        onValueChange = { viewModel.updateJumlah(it) },
                        label = { Text("Jumlah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.formState.isJumlahError,
                        supportingText = {
                            if (viewModel.formState.isJumlahError) {
                                Text("Jumlah tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Total Harga (Read Only)
                    OutlinedTextField(
                        value = if (viewModel.formState.totalHarga > 0)
                            formatRupiah.format(viewModel.formState.totalHarga)
                        else "",
                        onValueChange = {},
                        label = { Text("Total Harga") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { token?.let { viewModel.savePenjualan(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.penjualanFormUiState !is PenjualanFormUiState.Loading
            ) {
                if (viewModel.penjualanFormUiState is PenjualanFormUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}