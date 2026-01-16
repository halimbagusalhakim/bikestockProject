package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.EditNote
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
import com.example.bikestockproject.viewmodel.MerkFormUiState
import com.example.bikestockproject.viewmodel.MerkFormViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerkEditScreen(
    navigateBack: () -> Unit,
    viewModel: MerkFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    // Definisi Warna Konsisten
    val slate900 = Color(0xFF0F172A) // Untuk Teks & Judul
    val emerald600 = Color(0xFF059669) // Untuk Aksi (Hijau Emerald)
    val softWhite = Color(0xFFF8FAFC) // Untuk Background

    // Trigger muat data saat token siap
    LaunchedEffect(token) {
        token?.let { viewModel.loadDataUntukEdit(it) }
    }

    // Handle navigasi sukses
    LaunchedEffect(viewModel.merkFormUiState) {
        if (viewModel.merkFormUiState is MerkFormUiState.Success) {
            Toast.makeText(context, "Berhasil memperbarui merk", Toast.LENGTH_SHORT).show()
            navigateBack()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Perbarui Merk",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(softWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card Kontainer Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = emerald600,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Ubah Informasi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = viewModel.formState.namaMerk,
                            onValueChange = { viewModel.updateNamaMerk(it) },
                            label = { Text("Nama Merk") },
                            placeholder = { Text("Masukkan nama merk baru") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            isError = viewModel.formState.isNamaMerkError,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = emerald600,
                                focusedLabelColor = emerald600,
                                cursorColor = emerald600,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        if (viewModel.formState.isNamaMerkError) {
                            Text(
                                text = "Nama merk tidak boleh kosong",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Mendorong tombol ke bawah

                // Tombol Simpan (Konsisten Emerald)
                Button(
                    onClick = { token?.let { viewModel.saveMerk(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(18.dp),
                    enabled = viewModel.merkFormUiState !is MerkFormUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = emerald600,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    if (viewModel.merkFormUiState is MerkFormUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            "Simpan Perubahan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}