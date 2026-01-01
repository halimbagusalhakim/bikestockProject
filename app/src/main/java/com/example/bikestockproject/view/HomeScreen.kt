package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.bikestockproject.viewmodel.HomeViewModel
import com.example.bikestockproject.viewmodel.LogoutUiState
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToMerk: () -> Unit,
    navigateToProduk: () -> Unit,
    navigateToPenjualan: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    val username by tokenManager.username.collectAsState(initial = "")
    val token by tokenManager.token.collectAsState(initial = "")

    // Observe Logout State
    LaunchedEffect(viewModel.logoutUiState) {
        when (val state = viewModel.logoutUiState) {
            is LogoutUiState.Success -> {
                scope.launch {
                    tokenManager.clearAuthData()
                }
                Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                onLogout()
                viewModel.resetState()
            }
            is LogoutUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(token ?: "")
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Welcome Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Selamat Datang,",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = username ?: "Karyawan",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Menu Utama",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Cards
            MenuCard(
                title = "Kelola Merk",
                description = "Tambah, edit, dan hapus data merk sepeda",
                icon = Icons.Default.Star,
                onClick = navigateToMerk
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuCard(
                title = "Kelola Produk",
                description = "Kelola data produk sepeda dan stok",
                icon = Icons.Default.ShoppingCart,
                onClick = navigateToProduk
            )

            Spacer(modifier = Modifier.height(12.dp))

            MenuCard(
                title = "Catatan Penjualan",
                description = "Lihat dan kelola catatan penjualan",
                icon = Icons.Default.List,
                onClick = navigateToPenjualan
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}