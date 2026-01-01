package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.ProdukStokViewModel
import com.example.bikestockproject.viewmodel.UpdateStokUiState
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdukStokScreen(
    navigateBack: () -> Unit,
    viewModel: ProdukStokViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = "")

    LaunchedEffect(viewModel.updateStokUiState) {
        when (val state = viewModel.updateStokUiState) {
            is UpdateStokUiState.Success -> {
                Toast.makeText(context, "Stok berhasil diperbarui", Toast.LENGTH_SHORT).show()
                navigateBack()
            }
            is UpdateStokUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Stok") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Update Stok Produk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.formState.stokBaru,
                        onValueChange = { viewModel.updateStokBaru(it) },
                        label = { Text("Stok Baru") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.formState.isStokError,
                        supportingText = {
                            if (viewModel.formState.isStokError) {
                                Text("Stok tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { token?.let { viewModel.saveStok(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.updateStokUiState !is UpdateStokUiState.Loading
            ) {
                if (viewModel.updateStokUiState is UpdateStokUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}