package com.example.bikestockproject.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val token by tokenManager.token.collectAsState(initial = null)

    // Warna Tema Konsisten
    val slate900 = Color(0xFF0F172A) // Untuk Teks & Header
    val emerald600 = Color(0xFF059669) // Untuk Aksi Utama (Ijo Emerald)
    val softWhite = Color(0xFFF8FAFC) // Untuk Background

    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getPenjualanDetail(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detail Transaksi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = slate900
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
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
        when (val state = viewModel.penjualanDetailUiState) {
            is PenjualanDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = emerald600)
                }
            }
            is PenjualanDetailUiState.Success -> {
                val penjualan = state.penjualan
                val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                    maximumFractionDigits = 0
                }

                Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(softWhite)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // --- HEADER CARD (SLATE THEME) ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = slate900)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Total Pembayaran",
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = formatRupiah.format(penjualan.totalHarga),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = (-1).sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- DETAIL CARD ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Receipt, null, tint = emerald600, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Rincian Pesanan",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = slate900
                                        )
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF1F5F9))

                                DetailRowCustom(label = "ID Transaksi", value = "#${penjualan.penjualanId}", slateColor = slate900)
                                DetailRowCustom(label = "Nama Pembeli", value = penjualan.namaPembeli, slateColor = slate900)
                                DetailRowCustom(label = "Produk Sepeda", value = penjualan.namaProduk ?: "-", slateColor = slate900)
                                DetailRowCustom(label = "Jumlah Beli", value = "${penjualan.jumlah} Unit", slateColor = slate900)
                                DetailRowCustom(label = "Tanggal Transaksi", value = penjualan.tanggal ?: "-", slateColor = slate900)
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // --- ACTION BUTTONS (EMERALD) ---
                        Button(
                            onClick = { navigateToPenjualanEdit(penjualan.penjualanId!!) },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = emerald600),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(Icons.Default.EditNote, null, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Edit Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = navigateBack,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = slate900)
                        ) {
                            Text("Kembali ke Daftar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is PenjualanDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun DetailRowCustom(label: String, value: String, slateColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = slateColor
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}