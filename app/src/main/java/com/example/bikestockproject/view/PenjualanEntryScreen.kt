package com.example.bikestockproject.view

import android.widget.Toast
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
fun PenjualanEntryScreen(
    navigateBack: () -> Unit,
    viewModel: PenjualanFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)
    var expandedProduk by remember { mutableStateOf(false) }

    // Warna Tema Konsisten
    val slate900 = Color(0xFF0F172A) // Untuk Teks & Header
    val emerald600 = Color(0xFF059669) // Untuk Aksi (Hijau Emerald)
    val softWhite = Color(0xFFF8FAFC) // Untuk Background

    val formatRupiah = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getProdukList(it) }
    }

    LaunchedEffect(viewModel.penjualanFormUiState) {
        when (val state = viewModel.penjualanFormUiState) {
            is PenjualanFormUiState.Success -> {
                Toast.makeText(context, "Transaksi berhasil dicatat", Toast.LENGTH_SHORT).show()
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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Input Transaksi",
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(softWhite)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // --- RINGKASAN HARGA CARD (SLATE THEME) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = slate900)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Pembayaran", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                        Text(
                            text = if (viewModel.formState.totalHarga > 0) formatRupiah.format(viewModel.formState.totalHarga) else "Rp0",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-1).sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- FORM INPUT CARD ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Detail Penjualan",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = slate900
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        // Nama Pembeli
                        OutlinedTextField(
                            value = viewModel.formState.namaPembeli,
                            onValueChange = { viewModel.updateNamaPembeli(it) },
                            label = { Text("Nama Pembeli") },
                            leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp), tint = slate900) },
                            isError = viewModel.formState.isNamaPembeliError,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = emerald600,
                                focusedLabelColor = emerald600
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

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
                                    else -> "Pilih Produk..."
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pilih Produk Sepeda") },
                                leadingIcon = { Icon(Icons.Default.DirectionsBike, null, modifier = Modifier.size(20.dp), tint = slate900) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProduk) },
                                isError = viewModel.formState.isProdukIdError,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = emerald600,
                                    focusedLabelColor = emerald600
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedProduk,
                                onDismissRequest = { expandedProduk = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                when (val state = viewModel.produkDropdownUiState) {
                                    is ProdukDropdownUiState.Success -> {
                                        state.produkList.forEach { produk ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(produk.namaProduk, fontWeight = FontWeight.Bold, color = slate900)
                                                        Text(
                                                            text = "${formatRupiah.format(produk.harga)} • Stok: ${produk.stok}",
                                                            fontSize = 12.sp,
                                                            color = Color.Gray
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Jumlah
                        OutlinedTextField(
                            value = viewModel.formState.jumlah,
                            onValueChange = { viewModel.updateJumlah(it) },
                            label = { Text("Jumlah Unit") },
                            leadingIcon = { Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(20.dp), tint = slate900) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewModel.formState.isJumlahError,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = emerald600,
                                focusedLabelColor = emerald600
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL SIMPAN (EMERALD) ---
                Button(
                    onClick = { token?.let { viewModel.savePenjualan(it) } },
                    modifier = Modifier.fillMaxWidth().height(62.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = emerald600),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    enabled = viewModel.penjualanFormUiState !is PenjualanFormUiState.Loading
                ) {
                    if (viewModel.penjualanFormUiState is PenjualanFormUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
                    } else {
                        Icon(Icons.Default.AssignmentTurnedIn, null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Simpan Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}