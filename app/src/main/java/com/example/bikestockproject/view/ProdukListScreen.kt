package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    merkId: Int? = null, // Tambahan parameter untuk filter merk
    merkName: String? = null, // Nama merk untuk ditampilkan
    navigateToProdukEntry: () -> Unit,
    navigateToProdukDetail: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ProdukListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    var produkToDelete by remember { mutableStateOf<ProdukModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        token?.let {
            if (it.isNotEmpty()) {
                viewModel.getProdukList(it)
            }
        }
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
            title = { Text("Hapus Produk") },
            text = { Text("Yakin ingin menghapus produk ${produkToDelete?.namaProduk}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        token?.let {
                            produkToDelete?.produkId?.let { id ->
                                viewModel.deleteProduk(it, id)
                            }
                        }
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Daftar Produk")
                        if (merkName != null) {
                            Text(
                                text = "Merk: $merkName",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToProdukEntry) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        }
    ) { paddingValues ->
        when (val state = viewModel.produkListUiState) {
            is ProdukListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProdukListUiState.Success -> {
                // Filter produk berdasarkan merk jika merkId tidak null
                val filteredList = if (merkId != null) {
                    state.produkList.filter { it.merkId == merkId }
                } else {
                    state.produkList
                }

                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Belum ada data produk")
                            if (merkName != null) {
                                Text(
                                    text = "untuk merk $merkName",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produk.namaProduk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = produk.namaMerk ?: "-",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = formatRupiah.format(produk.harga),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " • ",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${produk.stok} Unit",
                        fontSize = 14.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}