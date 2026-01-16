package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.ProdukFormUiState
import com.example.bikestockproject.viewmodel.ProdukFormViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukEntryScreen(
    namaMerk: String,
    navigateBack: () -> Unit,
    viewModel: ProdukFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    // Warna Tema Konsisten
    val slate900 = Color(0xFF0F172A)
    val emerald600 = Color(0xFF059669)
    val softWhite = Color(0xFFF8FAFC)

    LaunchedEffect(viewModel.produkFormUiState) {
        when (val state = viewModel.produkFormUiState) {
            is ProdukFormUiState.Success -> {
                Toast.makeText(context, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tambah Produk",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = slate900
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Kembali",
                            modifier = Modifier.size(20.dp),
                            tint = slate900
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(softWhite)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsBike, null, tint = emerald600, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Spesifikasi Produk",
                            fontWeight = FontWeight.Bold,
                            color = slate900,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Field Merk (Read-Only)
                    OutlinedTextField(
                        value = namaMerk,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Merk Sepeda") },
                        leadingIcon = { Icon(Icons.Default.BrandingWatermark, null, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = softWhite,
                            focusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nama Produk
                    OutlinedTextField(
                        value = viewModel.formState.namaProduk,
                        onValueChange = { viewModel.updateNamaProduk(it) },
                        label = { Text("Nama Model/Seri") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        isError = viewModel.formState.isNamaProdukError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emerald600,
                            focusedLabelColor = emerald600
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Harga
                    OutlinedTextField(
                        value = viewModel.formState.harga,
                        onValueChange = { viewModel.updateHarga(it) },
                        label = { Text("Harga (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        isError = viewModel.formState.isHargaError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emerald600,
                            focusedLabelColor = emerald600
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Stok
                    Text(
                        text = "Stok Gudang",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (viewModel.formState.isStokError) Color.Red else slate900
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Kurang (-)
                        Surface(
                            onClick = {
                                val current = viewModel.formState.stok.toIntOrNull() ?: 0
                                if (current > 0) viewModel.updateStok((current - 1).toString())
                            },
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = softWhite,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Remove, null, tint = slate900)
                            }
                        }

                        // Input Teks Stok
                        OutlinedTextField(
                            value = viewModel.formState.stok,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    viewModel.updateStok(it)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = slate900
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewModel.formState.isStokError,
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = emerald600
                            )
                        )

                        // Tombol Tambah (+)
                        Surface(
                            onClick = {
                                val current = viewModel.formState.stok.toIntOrNull() ?: 0
                                viewModel.updateStok((current + 1).toString())
                            },
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = emerald600
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Add, null, tint = Color.White)
                            }
                        }
                    }

                    if (viewModel.formState.isStokError) {
                        Text(
                            text = "Stok tidak boleh kosong",
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Deskripsi
                    OutlinedTextField(
                        value = viewModel.formState.deskripsi,
                        onValueChange = { viewModel.updateDeskripsi(it) },
                        label = { Text("Deskripsi Singkat (Opsional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = emerald600,
                            focusedLabelColor = emerald600
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Simpan
            Button(
                onClick = { token?.let { viewModel.saveProduk(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = emerald600),
                enabled = viewModel.produkFormUiState !is ProdukFormUiState.Loading
            ) {
                if (viewModel.produkFormUiState is ProdukFormUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        "Simpan Katalog Produk",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}