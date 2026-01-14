package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.MerkDropdownUiState
import com.example.bikestockproject.viewmodel.ProdukFormUiState
import com.example.bikestockproject.viewmodel.ProdukFormViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukEditScreen(
    navigateBack: () -> Unit,
    viewModel: ProdukFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)
    var expandedMerk by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        token?.let {
            if (it.isNotEmpty()) {
                viewModel.loadProdukData(it)
                viewModel.getMerkList(it)
            }
        }
    }

    LaunchedEffect(viewModel.produkFormUiState) {
        when (val state = viewModel.produkFormUiState) {
            is ProdukFormUiState.Success -> {
                Toast.makeText(context, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
                navigateBack()
            }
            is ProdukFormUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Produk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Kembali", modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF8F9FA))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- FORM CARD ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Informasi Produk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 1. Dropdown Merk
                        ExposedDropdownMenuBox(
                            expanded = expandedMerk,
                            onExpandedChange = { expandedMerk = it }
                        ) {
                            OutlinedTextField(
                                value = when (val state = viewModel.merkDropdownUiState) {
                                    is MerkDropdownUiState.Success -> {
                                        state.merkList.find { it.merkId == viewModel.formState.merkId }?.namaMerk ?: ""
                                    }
                                    else -> "Memuat merk..."
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Merk Sepeda") },
                                leadingIcon = { Icon(Icons.Default.BrandingWatermark, null, modifier = Modifier.size(20.dp)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMerk) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandedMerk,
                                onDismissRequest = { expandedMerk = false }
                            ) {
                                if (viewModel.merkDropdownUiState is MerkDropdownUiState.Success) {
                                    (viewModel.merkDropdownUiState as MerkDropdownUiState.Success).merkList.forEach { merk ->
                                        DropdownMenuItem(
                                            text = { Text(merk.namaMerk) },
                                            onClick = {
                                                viewModel.updateMerkId(merk.merkId!!)
                                                expandedMerk = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Nama Produk
                        OutlinedTextField(
                            value = viewModel.formState.namaProduk,
                            onValueChange = { viewModel.updateNamaProduk(it) },
                            label = { Text("Nama Produk") },
                            leadingIcon = { Icon(Icons.Default.DirectionsBike, null, modifier = Modifier.size(20.dp)) },
                            isError = viewModel.formState.isNamaProdukError,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Harga
                        OutlinedTextField(
                            value = viewModel.formState.harga,
                            onValueChange = { viewModel.updateHarga(it) },
                            label = { Text("Harga (Rp)") },
                            leadingIcon = { Icon(Icons.Default.Payments, null, modifier = Modifier.size(20.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = viewModel.formState.isHargaError,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // 4. Kontrol Stok Modern
                        Text("Pengaturan Stok", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    val current = viewModel.formState.stok.toIntOrNull() ?: 0
                                    if (current > 0) viewModel.updateStok((current - 1).toString())
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.error)
                                }
                            }

                            OutlinedTextField(
                                value = viewModel.formState.stok,
                                onValueChange = { if (it.all { c -> c.isDigit() }) viewModel.updateStok(it) },
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(fontSize = 18.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = viewModel.formState.isStokError,
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Surface(
                                onClick = {
                                    val current = viewModel.formState.stok.toIntOrNull() ?: 0
                                    viewModel.updateStok((current + 1).toString())
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE8F5E9), // Warna hijau muda soft
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Add, null, tint = Color(0xFF2E7D32))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5. Deskripsi
                        OutlinedTextField(
                            value = viewModel.formState.deskripsi,
                            onValueChange = { viewModel.updateDeskripsi(it) },
                            label = { Text("Deskripsi Tambahan (Opsional)") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- ACTION BUTTON ---
                Button(
                    onClick = { token?.let { viewModel.saveProduk(it) } },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = viewModel.produkFormUiState !is ProdukFormUiState.Loading
                ) {
                    if (viewModel.produkFormUiState is ProdukFormUiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}