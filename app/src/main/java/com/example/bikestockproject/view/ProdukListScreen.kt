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

    // Fetch data logic
    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getProdukList(it) }
    }

    // Handle Delete State
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
            icon = { Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus '${produkToDelete?.namaProduk}'? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        token?.let { tkn -> produkToDelete?.produkId?.let { id -> viewModel.deleteProduk(tkn, id) } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Daftar Produk", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = if (merkName != null) merkName else "Semua Merk",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Kembali", modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (merkId != null && merkName != null) {
                ExtendedFloatingActionButton(
                    onClick = { navigateToProdukEntry(merkId, merkName) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Tambah") },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF8F9FA))) {
            when (val state = viewModel.produkListUiState) {
                is ProdukListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProdukListUiState.Success -> {
                    val filteredList = if (merkId != null) state.produkList.filter { it.merkId == merkId } else state.produkList

                    if (filteredList.isEmpty()) {
                        EmptyStateProduk(merkName)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredList) { produk ->
                                ProdukCard(
                                    produk = produk,
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
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formatRupiah = remember {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    // Status Warna Stok
    val stokColor = if (produk.stok > 10) Color(0xFF10B981) else Color(0xFFEF4444)
    val stokBg = stokColor.copy(alpha = 0.1f)

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Color(0xFFF1F3F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder Gambar / Icon Sepeda
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.DirectionsBike, null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produk.namaProduk,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatRupiah.format(produk.harga),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge Stok
                Surface(
                    shape = CircleShape,
                    color = stokBg,
                    border = BorderStroke(1.dp, stokColor.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(stokColor, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${produk.stok} Unit Tersedia",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = stokColor
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.background(Color(0xFFFFF1F2), CircleShape).size(36.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun EmptyStateProduk(merkName: String?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF1F3F5),
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Produk Kosong", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Belum ada produk untuk ${merkName ?: "kategori ini"}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}