package com.example.bikestockproject.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bikestockproject.R
import com.example.bikestockproject.local.TokenManager
import com.example.bikestockproject.viewmodel.LoginUiState
import com.example.bikestockproject.viewmodel.LoginViewModel
import com.example.bikestockproject.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.delay
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

    // Warna Modern Slate & Emerald
    val slate900 = Color(0xFF0F172A)
    val emeraldAccent = Color(0xFF10B981)
    val softWhite = Color(0xFFF8FAFC)

    val loginSuccessMsg = stringResource(R.string.login_success)

    LaunchedEffect(viewModel.loginUiState) {
        when (val state = viewModel.loginUiState) {
            is LoginUiState.Success -> {
                val token = state.data.data?.token ?: ""
                if (token.isNotEmpty()) {
                    scope.launch {
                        tokenManager.saveAuthData(token, 1, viewModel.formState.username, "user")
                    }
                    Toast.makeText(context, loginSuccessMsg, Toast.LENGTH_SHORT).show()
                    delay(300)
                    navigateToHome()
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

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(softWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // Header Area
                Text(
                    text = stringResource(R.string.login_header),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = slate900,
                        letterSpacing = (-1).sp
                    )
                )
                Text(
                    text = stringResource(R.string.login_subheader),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Username Field
                OutlinedTextField(
                    value = viewModel.formState.username,
                    onValueChange = { viewModel.updateUsername(it) },
                    label = { Text(stringResource(R.string.label_username)) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = slate900)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    isError = viewModel.formState.isUsernameError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = emeraldAccent,
                        focusedLabelColor = emeraldAccent,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = emeraldAccent
                    )
                )

                if (viewModel.formState.isUsernameError) {
                    Text(
                        text = stringResource(R.string.error_username_empty),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                OutlinedTextField(
                    value = viewModel.formState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text(stringResource(R.string.label_password)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = slate900)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    isError = viewModel.formState.isPasswordError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = emeraldAccent,
                        focusedLabelColor = emeraldAccent,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        cursorColor = emeraldAccent
                    )
                )

                if (viewModel.formState.isPasswordError) {
                    Text(
                        text = stringResource(R.string.error_password_empty),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                // Bagian Lupa Password sudah dihapus untuk tampilan yang lebih minimalis

                Spacer(modifier = Modifier.height(48.dp))

                // Login Button
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),
                    shape = RoundedCornerShape(18.dp),
                    enabled = viewModel.loginUiState !is LoginUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = emeraldAccent,
                        contentColor = Color.White,
                        disabledContainerColor = emeraldAccent.copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    if (viewModel.loginUiState is LoginUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.btn_login),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Opsional: Text bantuan di bawah jika user belum punya akun
                // (Anda bisa menambahkan navigasi Register di sini nanti)
            }
        }
    }
}