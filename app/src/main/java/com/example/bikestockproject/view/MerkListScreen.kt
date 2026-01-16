package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.modeldata.MerkModel
import com.example.bikestockproject.viewmodel.DeleteMerkUiState
import com.example.bikestockproject.viewmodel.MerkListUiState
import com.example.bikestockproject.viewmodel.MerkListViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerkListScreen(
    navigateToMerkEntry: () -> Unit,
    navigateToMerkEdit: (Int) -> Unit,
    navigateBack: () -> Unit,
    navigateToProdukByMerk: (Int, String) -> Unit,
    viewModel: MerkListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    var merkToDelete by remember { mutableStateOf<MerkModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Definisi Warna Konsisten
    val slate900 = Color(0xFF0F172A) // Untuk Teks Utama & Header
    val emerald600 = Color(0xFF059669) // Untuk Aksi (Hijau)
    val softWhite = Color(0xFFF8FAFC) // Untuk Background

    LaunchedEffect(token) {
        token?.let { if (it.isNotEmpty()) viewModel.getMerkList(it) }
    }

    LaunchedEffect(viewModel.deleteMerkUiState) {
        when (val state = viewModel.deleteMerkUiState) {
            is DeleteMerkUiState.Success -> {
                Toast.makeText(context, "Merk berhasil dihapus", Toast.LENGTH_SHORT).show()
                token?.let { viewModel.getMerkList(it) }
                viewModel.resetDeleteState()
            }
            is DeleteMerkUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    if (showDeleteDialog && merkToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Merk", fontWeight = FontWeight.Bold, color = slate900) },
            text = { Text("Yakin ingin menghapus merk ${merkToDelete?.namaMerk}?", color = slate900) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        token?.let { t -> merkToDelete?.merkId?.let { id -> viewModel.deleteMerk(t, id) } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = emerald600), // Tombol Konfirmasi Hijau
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
                        Text(
                            "Daftar Merk",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = slate900
                            )
                        )
                        Text(
                            "Kelola Kategori Sepeda",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
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
                onClick = navigateToMerkEntry,
                containerColor = emerald600, // Tombol Tambah Hijau
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(text = "Tambah Merk", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(softWhite)) {
            when (val state = viewModel.merkListUiState) {
                is MerkListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = emerald600 // Loading Hijau
                    )
                }
                is MerkListUiState.Success -> {
                    if (state.merkList.isEmpty()) {
                        EmptyStateMerk(slate900)
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.merkList) { merk ->
                                MerkGridCard(
                                    merk = merk,
                                    accentColor = emerald600,
                                    textColor = slate900,
                                    onClick = { navigateToProdukByMerk(merk.merkId!!, merk.namaMerk) },
                                    onEdit = { navigateToMerkEdit(merk.merkId!!) },
                                    onDelete = {
                                        merkToDelete = merk
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is MerkListUiState.Error -> {
                    Text(
                        "Gagal memuat data",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun MerkGridCard(
    merk: MerkModel,
    accentColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(0.9f),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Menu Edit/Hapus
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.LightGray)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Data") },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus Merk", color = Color.Red) },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Red) }
                    )
                }
            }

            // Lingkaran Ikon (Aksen Hijau Pudar)
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.1f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Teks Nama Merk (Warna Hitam/Slate)
            Column(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = merk.namaMerk,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Badge Informasi (Netral Abu-abu)
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Lihat Produk",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateMerk(textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFFE2E8F0)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Belum Ada Merk",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
        Text(
            "Ketuk tombol + untuk menambah kategori",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}