package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.LoginUiState
import com.example.bikestockproject.viewmodel.LoginViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()

    var passwordVisible by remember { mutableStateOf(false) }

    // Observe UI State
    LaunchedEffect(viewModel.loginUiState) {
        when (val state = viewModel.loginUiState) {
            is LoginUiState.Success -> {
                // Simpan token dan data user
                val token = state.data.data?.token ?: ""
                if (token.isNotEmpty()) {
                    scope.launch {
                        tokenManager.saveAuthData(
                            token = token,
                            userId = 1,
                            username = viewModel.formState.username,
                            role = "user"
                        )
                    }
                    Toast.makeText(context, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    // Delay sedikit untuk memastikan token tersimpan
                    kotlinx.coroutines.delay(300)
                    navigateToHome()
                } else {
                    Toast.makeText(context, "Token kosong", Toast.LENGTH_SHORT).show()
                }
                viewModel.resetState()
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Selamat Datang",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Silakan login untuk melanjutkan",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Username Field
            OutlinedTextField(
                value = viewModel.formState.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = { Text("Username") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                isError = viewModel.formState.isUsernameError,
                supportingText = {
                    if (viewModel.formState.isUsernameError) {
                        Text("Username tidak boleh kosong")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = viewModel.formState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password"
                            else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = viewModel.formState.isPasswordError,
                supportingText = {
                    if (viewModel.formState.isPasswordError) {
                        Text("Password tidak boleh kosong")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.loginUiState !is LoginUiState.Loading
            ) {
                if (viewModel.loginUiState is LoginUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}