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
import com.example.bikestockproject.viewmodel.ProdukDetailUiState
import com.example.bikestockproject.viewmodel.ProdukDetailViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukDetailScreen(
    navigateToProdukEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ProdukDetailViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    // Warna Tema Konsisten
    val slate900 = Color(0xFF0F172A) // Untuk Teks & Judul
    val emerald600 = Color(0xFF059669) // Untuk Aksi Utama (Tombol)
    val softWhite = Color(0xFFF8FAFC) // Untuk Background

    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getProdukDetail(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detail Produk",
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
        when (val state = viewModel.produkDetailUiState) {
            is ProdukDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = emerald600)
                }
            }
            is ProdukDetailUiState.Success -> {
                val produk = state.produk
                val formatRupiah = remember {
                    NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                        maximumFractionDigits = 0
                    }
                }

                Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(softWhite)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // Header Visual
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            shape = RoundedCornerShape(32.dp),
                            color = slate900.copy(alpha = 0.05f),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),
                                    tint = slate900.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Nama Produk
                        Text(
                            text = produk.namaProduk,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = slate900,
                                letterSpacing = (-1).sp
                            )
                        )

                        // Harga (Menggunakan Slate agar tidak mengalihkan perhatian dari tombol aksi)
                        Text(
                            text = formatRupiah.format(produk.harga),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = slate900,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Stats Grid (Stok & Merk)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            InfoBadge(
                                modifier = Modifier.weight(1f),
                                label = "Stok Gudang",
                                value = "${produk.stok} Unit",
                                icon = Icons.Default.Inventory2,
                                // Hijau Emerald jika stok aman, Merah jika kritis
                                color = if (produk.stok > 5) emerald600 else Color(0xFFE11D48),
                                slateColor = slate900
                            )
                            InfoBadge(
                                modifier = Modifier.weight(1f),
                                label = "Merk Sepeda",
                                value = produk.namaMerk ?: "-",
                                icon = Icons.Default.BrandingWatermark,
                                color = slate900,
                                slateColor = slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Deskripsi Section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notes, null, modifier = Modifier.size(20.dp), tint = slate900)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Deskripsi Produk",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = slate900
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = produk.deskripsi ?: "Informasi deskripsi belum ditambahkan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Action Button (KONSISTEN EMERALD)
                        Button(
                            onClick = { navigateToProdukEdit(produk.produkId!!) },
                            modifier = Modifier.fillMaxWidth().height(62.dp),
                            shape = RoundedCornerShape(18.dp),
                            // Diubah menjadi emerald600 agar konsisten dengan seluruh aplikasi
                            colors = ButtonDefaults.buttonColors(containerColor = emerald600),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Edit Spesifikasi Produk", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            is ProdukDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun InfoBadge(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    slateColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, modifier = Modifier.size(20.dp), tint = color)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(
                value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = slateColor
                )
            )
        }
    }
}