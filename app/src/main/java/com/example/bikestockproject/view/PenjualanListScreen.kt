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
    val token by tokenManager.token.collectAsState(initial = null)

    var penjualanToDelete by remember { mutableStateOf<PenjualanModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Warna Tema Konsisten
    val slate900 = Color(0xFF0F172A) // Teks & Judul
    val emerald600 = Color(0xFF059669) // Aksi Utama (Ijo Emerald)
    val softWhite = Color(0xFFF8FAFC) // Background

    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getPenjualanList(it) }
    }

    LaunchedEffect(viewModel.deletePenjualanUiState) {
        when (val state = viewModel.deletePenjualanUiState) {
            is DeletePenjualanUiState.Success -> {
                Toast.makeText(context, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show()
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
            title = { Text("Hapus Transaksi", fontWeight = FontWeight.Bold, color = slate900) },
            text = { Text("Hapus data penjualan atas nama ${penjualanToDelete?.namaPembeli}?", color = slate900) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        token?.let { tkn ->
                            penjualanToDelete?.penjualanId?.let { id -> viewModel.deletePenjualan(tkn, id) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = emerald600),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Ya, Hapus") }
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
                        Text(
                            "Riwayat Penjualan",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = slate900
                            )
                        )
                        Text(
                            "Log Transaksi Toko",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = navigateToPenjualanEntry,
                containerColor = emerald600,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.PostAdd, null) },
                text = { Text("Transaksi Baru", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(softWhite)) {
            when (val state = viewModel.penjualanListUiState) {
                is PenjualanListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = emerald600)
                }
                is PenjualanListUiState.Success -> {
                    if (state.penjualanList.isEmpty()) {
                        EmptyStatePenjualan(slate900)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.penjualanList) { penjualan ->
                                PenjualanCard(
                                    penjualan = penjualan,
                                    accentColor = emerald600,
                                    slateColor = slate900,
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
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun PenjualanCard(
    penjualan: PenjualanModel,
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
            // Inisial Pembeli dengan Style Bulat Slate
            Surface(
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = slateColor.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = penjualan.namaPembeli.take(1).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = slateColor,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = penjualan.namaPembeli,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = slateColor,
                        letterSpacing = (-0.5).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = penjualan.namaProduk ?: "Produk Tidak Diketahui",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRupiah.format(penjualan.totalHarga),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
                    Text(
                        text = " • ${penjualan.jumlah} Unit",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color(0xFFFFF1F2), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    Icons.Default.DeleteOutline,
                    null,
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStatePenjualan(textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFFF1F5F9)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.ReceiptLong,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.LightGray
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Belum Ada Transaksi",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            "Mulai catat penjualan sepeda pertama Anda",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}