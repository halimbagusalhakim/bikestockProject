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
    viewModel: MerkListViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = "")

    var merkToDelete by remember { mutableStateOf<MerkModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load data
    LaunchedEffect(Unit) {
        token?.let { viewModel.getMerkList(it) }
    }

    // Observe delete state
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

    // Delete Confirmation Dialog
    if (showDeleteDialog && merkToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Merk") },
            text = { Text("Yakin ingin menghapus merk ${merkToDelete?.namaMerk}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        token?.let {
                            merkToDelete?.merkId?.let { id ->
                                viewModel.deleteMerk(it, id)
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
                title = { Text("Daftar Merk") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToMerkEntry) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Merk")
            }
        }
    ) { paddingValues ->
        when (val state = viewModel.merkListUiState) {
            is MerkListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is MerkListUiState.Success -> {
                if (state.merkList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada data merk")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.merkList) { merk ->
                            MerkCard(
                                merk = merk,
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
fun MerkCard(
    merk: MerkModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                    text = merk.namaMerk,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "ID: ${merk.merkId}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
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
}