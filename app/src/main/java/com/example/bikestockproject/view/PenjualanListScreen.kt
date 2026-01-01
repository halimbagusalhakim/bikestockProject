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
import com.example.bikestockproject.modeldata.PenjualanModel
import com.example.bikestockproject.viewmodel.DeletePenjualanUiState
import com.example.bikestockproject.viewmodel.PenjualanListUiState
import com.example.bikestockproject.viewmodel.PenjualanListViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenjualanListScreen(
    navigateToPenjualanEntry: () -> Unit,
    navigateToPenjualanDetail: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: PenjualanListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = "")

    var penjualanToDelete by remember { mutableStateOf<PenjualanModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        token?.let { viewModel.getPenjualanList(it) }
    }

    LaunchedEffect(viewModel.deletePenjualanUiState) {
        when (val state = viewModel.deletePenjualanUiState) {
            is DeletePenjualanUiState.Success -> {
                Toast.makeText(context, "Penjualan berhasil dihapus", Toast.LENGTH_SHORT).show()
                token?.let { viewModel.getPenjualanList(it) }
                viewModel.resetDeleteState()
            }
            is DeletePenjualanUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    if (showDeleteDialog && penjualanToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Penjualan") },
            text = { Text("Yakin ingin menghapus penjualan ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        token?.let {
                            penjualanToDelete?.penjualanId?.let { id ->
                                viewModel.deletePenjualan(it, id)
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
                title = { Text("Daftar Penjualan") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToPenjualanEntry) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Penjualan")
            }
        }
    ) { paddingValues ->
        when (val state = viewModel.penjualanListUiState) {
            is PenjualanListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PenjualanListUiState.Success -> {
                if (state.penjualanList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada data penjualan")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.penjualanList) { penjualan ->
                            PenjualanCard(
                                penjualan = penjualan,
                                onClick = { navigateToPenjualanDetail(penjualan.penjualanId!!) },
                                onDelete = {
                                    penjualanToDelete = penjualan
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
            is PenjualanListUiState.Error -> {
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
fun PenjualanCard(
    penjualan: PenjualanModel,
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
                    text = penjualan.namaPembeli,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = penjualan.namaProduk ?: "-",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = formatRupiah.format(penjualan.totalHarga),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " • ${penjualan.jumlah} unit",
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