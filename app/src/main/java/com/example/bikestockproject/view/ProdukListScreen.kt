package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.modeldata.ProdukModel
import com.example.bikestockproject.viewmodel.DeleteProdukUiState
import com.example.bikestockproject.viewmodel.ProdukListUiState
import com.example.bikestockproject.viewmodel.ProdukListViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukListScreen(
    merkId: Int? = null,
    merkName: String? = null,
    navigateToProdukEntry: (Int, String) -> Unit,
    navigateToProdukDetail: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ProdukListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    var produkToDelete by remember { mutableStateOf<ProdukModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Warna Tema Konsisten
    val slate900 = Color(0xFF0F172A) // Teks & Judul
    val emerald600 = Color(0xFF059669) // Aksi Utama (Tombol Tambah)
    val softWhite = Color(0xFFF8FAFC) // Background

    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getProdukList(it) }
    }

    LaunchedEffect(viewModel.deleteProdukUiState) {
        when (val state = viewModel.deleteProdukUiState) {
            is DeleteProdukUiState.Success -> {
                Toast.makeText(context, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                token?.let { viewModel.getProdukList(it) }
                viewModel.resetDeleteState()
            }
            is DeleteProdukUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    if (showDeleteDialog && produkToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Produk", fontWeight = FontWeight.Bold, color = slate900) },
            text = { Text("Yakin ingin menghapus '${produkToDelete?.namaProduk}'?", color = slate900) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        token?.let { tkn -> produkToDelete?.produkId?.let { id -> viewModel.deleteProduk(tkn, id) } }
                    },
                    // Menggunakan Emerald Green agar senada dengan tombol konfirmasi lainnya
                    colors = ButtonDefaults.buttonColors(containerColor = emerald600),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Ya, Hapus", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Daftar Produk", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = slate900))
                        Text(
                            text = if (merkName != null) "Koleksi $merkName" else "Semua Katalog",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(20.dp), tint = slate900)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (merkId != null && merkName != null) {
                ExtendedFloatingActionButton(
                    onClick = { navigateToProdukEntry(merkId, merkName) },
                    // DIUBAH: Menggunakan emerald600 agar konsisten dengan tombol aksi lainnya
                    containerColor = emerald600,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Tambah Produk", fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(softWhite)) {
            when (val state = viewModel.produkListUiState) {
                is ProdukListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = emerald600)
                }
                is ProdukListUiState.Success -> {
                    val filteredList = if (merkId != null) state.produkList.filter { it.merkId == merkId } else state.produkList

                    if (filteredList.isEmpty()) {
                        EmptyStateProduk(merkName, slate900)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredList) { produk ->
                                ProdukCard(
                                    produk = produk,
                                    accentColor = emerald600,
                                    slateColor = slate900,
                                    onClick = { navigateToProdukDetail(produk.produkId!!) },
                                    onDelete = {
                                        produkToDelete = produk
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is ProdukListUiState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun ProdukCard(
    produk: ProdukModel,
    accentColor: Color,
    slateColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formatRupiah = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    // Indikator Stok Kritis (< 5)
    val isCritical = produk.stok < 5
    val stokStatusColor = if (isCritical) Color(0xFFE11D48) else Color.Gray

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Square Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = slateColor.copy(alpha = 0.05f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBike, null, tint = slateColor, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produk.namaProduk,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = slateColor,
                        letterSpacing = (-0.5).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatRupiah.format(produk.harga),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = slateColor // Diubah ke slate agar Emerald fokus pada aksi tombol
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Badge Stok Minimalis
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(stokStatusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Stok: ${produk.stok} unit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = stokStatusColor
                        )
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color(0xFFFFF1F2), CircleShape)
                    .size(32.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFE11D48), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EmptyStateProduk(merkName: String?, textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF1F5F9),
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Belum Ada Produk", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
        Text(
            "Kategori ${merkName ?: "ini"} belum memiliki data",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}