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
import com.example.bikestockproject.viewmodel.MerkDropdownUiState
import com.example.bikestockproject.viewmodel.ProdukFormUiState
import com.example.bikestockproject.viewmodel.ProdukFormViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukEditScreen(
    navigateBack: () -> Unit,
    viewModel: ProdukFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    var expandedMerk by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        token?.let {
            if (it.isNotEmpty()) {
                viewModel.getMerkList(it)
            }
        }
    }

    LaunchedEffect(viewModel.produkFormUiState) {
        when (val state = viewModel.produkFormUiState) {
            is ProdukFormUiState.Success -> {
                Toast.makeText(context, "Produk berhasil diubah", Toast.LENGTH_SHORT).show()
                navigateBack()
            }
            is ProdukFormUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Produk") },
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
                        text = "Informasi Produk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown Merk
                    ExposedDropdownMenuBox(
                        expanded = expandedMerk,
                        onExpandedChange = { expandedMerk = it }
                    ) {
                        OutlinedTextField(
                            value = when (val state = viewModel.merkDropdownUiState) {
                                is MerkDropdownUiState.Success -> {
                                    state.merkList.find { it.merkId == viewModel.formState.merkId }?.namaMerk ?: ""
                                }
                                else -> ""
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Merk") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            isError = viewModel.formState.isMerkIdError,
                            supportingText = {
                                if (viewModel.formState.isMerkIdError) {
                                    Text("Merk harus dipilih")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedMerk,
                            onDismissRequest = { expandedMerk = false }
                        ) {
                            when (val state = viewModel.merkDropdownUiState) {
                                is MerkDropdownUiState.Success -> {
                                    state.merkList.forEach { merk ->
                                        DropdownMenuItem(
                                            text = { Text(merk.namaMerk) },
                                            onClick = {
                                                viewModel.updateMerkId(merk.merkId!!)
                                                expandedMerk = false
                                            }
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.formState.namaProduk,
                        onValueChange = { viewModel.updateNamaProduk(it) },
                        label = { Text("Nama Produk") },
                        isError = viewModel.formState.isNamaProdukError,
                        supportingText = {
                            if (viewModel.formState.isNamaProdukError) {
                                Text("Nama produk tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.formState.harga,
                        onValueChange = { viewModel.updateHarga(it) },
                        label = { Text("Harga") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.formState.isHargaError,
                        supportingText = {
                            if (viewModel.formState.isHargaError) {
                                Text("Harga tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.formState.stok,
                        onValueChange = { viewModel.updateStok(it) },
                        label = { Text("Stok") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.formState.isStokError,
                        supportingText = {
                            if (viewModel.formState.isStokError) {
                                Text("Stok tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.formState.deskripsi,
                        onValueChange = { viewModel.updateDeskripsi(it) },
                        label = { Text("Deskripsi (Opsional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { token?.let { viewModel.saveProduk(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.produkFormUiState !is ProdukFormUiState.Loading
            ) {
                if (viewModel.produkFormUiState is ProdukFormUiState.Loading) {
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