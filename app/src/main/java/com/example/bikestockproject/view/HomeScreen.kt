package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    // Palette Warna Splash Screen (Slate & Emerald)
    val slate900 = Color(0xFF0F172A)
    val slate800 = Color(0xFF1E293B)
    val emeraldAccent = Color(0xFF10B981) // Hijau Emerald dari Splash
    val softWhite = Color(0xFFF8FAFC)

    // Gradasi Linear ala Splash Screen
    val headerGradient = Brush.linearGradient(
        colors = listOf(slate900, slate800)
    )

    LaunchedEffect(viewModel.logoutUiState) {
        if (viewModel.logoutUiState is LogoutUiState.Success) {
            scope.launch { tokenManager.clearAuthData() }
            onLogout()
            viewModel.resetState()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar Aplikasi", fontWeight = FontWeight.Black, color = slate900) },
            text = { Text("Apakah Anda yakin ingin mengakhiri sesi ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(token ?: "")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)), // Merah untuk logout
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Ya, Keluar", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    Scaffold(
        containerColor = softWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- HEADER MODERN SLATE (MIRIP SPLASH) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = headerGradient,
                        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 56.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Bekerja,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = username ?: "User",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-1).sp
                            )
                        )
                    }

                    // Logout Button dengan Aksen Emerald transparan
                    Surface(
                        onClick = { showLogoutDialog = true },
                        color = emeraldAccent.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = emeraldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- MENU AREA ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
            ) {
                Text(
                    text = "Layanan Utama",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = slate900,
                        letterSpacing = (-0.5).sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Menu 1: Manajemen Produk (Slate Style)
                    GridMenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Manajemen Produk",
                        description = "Kelola Inventaris",
                        icon = Icons.Default.DirectionsBike,
                        accentColor = slate900,
                        onClick = navigateToMerk
                    )

                    // Menu 2: Laporan Penjualan (Emerald Style)
                    GridMenuCard(
                        modifier = Modifier.weight(1f),
                        title = "Riwayat Penjualan",
                        description = "Pantau Transaksi",
                        icon = Icons.Default.ReceiptLong,
                        accentColor = emeraldAccent,
                        onClick = navigateToPenjualan
                    )
                }
            }
        }
    }
}

@Composable
fun GridMenuCard(
    modifier: Modifier,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(0.85f),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start // Rata kiri lebih modern
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.08f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}