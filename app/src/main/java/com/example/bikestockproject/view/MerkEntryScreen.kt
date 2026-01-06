package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
fun MerkEntryScreen(
    navigateBack: () -> Unit,
    viewModel: MerkFormViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token by tokenManager.token.collectAsState(initial = null)

    // Observe save state
    LaunchedEffect(viewModel.merkFormUiState) {
        when (val state = viewModel.merkFormUiState) {
            is MerkFormUiState.Success -> {
                Toast.makeText(context, "Merk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                navigateBack()
            }
            is MerkFormUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Merk") },
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
                        text = "Informasi Merk",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.formState.namaMerk,
                        onValueChange = { viewModel.updateNamaMerk(it) },
                        label = { Text("Nama Merk") },
                        isError = viewModel.formState.isNamaMerkError,
                        supportingText = {
                            if (viewModel.formState.isNamaMerkError) {
                                Text("Nama merk tidak boleh kosong")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { token?.let { viewModel.saveMerk(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.merkFormUiState !is MerkFormUiState.Loading
            ) {
                if (viewModel.merkFormUiState is MerkFormUiState.Loading) {
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