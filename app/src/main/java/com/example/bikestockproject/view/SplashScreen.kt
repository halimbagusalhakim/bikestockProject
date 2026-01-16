package com.example.bikestockproject.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikestockproject.R

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit
) {
    // Definisi Warna Slate & Emerald Premium
    val slate900 = Color(0xFF0F172A)
    val slate800 = Color(0xFF1E293B)
    val emerald600 = Color(0xFF059669) // Emerald yang lebih solid
    val emeraldAksen = Color(0xFF10B981)

    // Gradasi background dari pojok kiri atas ke kanan bawah
    val gradientBackground = Brush.linearGradient(
        colors = listOf(slate900, slate800, slate900)
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
        ) {
            // Dekorasi Lingkaran Abstrak di Latar Belakang
            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = (-50).dp)
                    .size(200.dp)
                    .background(emeraldAksen.copy(alpha = 0.05f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Spacer bagian atas agar konten tengah lebih seimbang
                Spacer(modifier = Modifier.height(20.dp))

                // --- CENTER CONTENT ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    // CONTAINER LOGO (Gunakan Image jika sudah ada fotonya)
                    Surface(
                        modifier = Modifier.size(140.dp),
                        shape = RoundedCornerShape(40.dp),
                        color = Color.White.copy(alpha = 0.03f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // CARA PAKAI FOTO:
                            Image(
                                painter = painterResource(id = R.drawable.logo_toko),
                                contentDescription = "Logo",
                                modifier = Modifier.size(90.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = stringResource(id = R.string.splash_title),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            lineHeight = 40.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.splash_tagline),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

                // --- BOTTOM CONTENT (TOMBOL) ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onGetStarted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = emerald600,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.btn_get_started),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Label Versi yang lebih estetik
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "VERSION 1.0.0",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}