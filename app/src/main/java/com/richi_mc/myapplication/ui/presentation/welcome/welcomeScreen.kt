package com.richi_mc.myapplication.ui.presentation.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun welcomeScreen(
    viewModel: welcomeScreenViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    var nameInput by remember { mutableStateOf("") }

    // Navegación automática cuando el estado es Success
    LaunchedEffect(state) {
        if (state is SplashState.Success) {
            onNavigateToMain()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Financial Scan",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tu salud financiera con IA",
            color = Color(0xFF888888),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedContent(targetState = state, label = "splash_states") { currentState ->
            when (currentState) {
                is SplashState.Loading -> {
                    CircularProgressIndicator(color = Color(0xFF4ADE80))
                }

                is SplashState.RequireName -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("¿Cómo te llamas?", color = Color.White, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Ej. Ricardo", color = Color(0xFF555555)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4ADE80),
                                unfocusedBorderColor = Color(0xFF333333),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.registerUser(nameInput) },
                            enabled = nameInput.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
                        ) {
                            Text("Comenzar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                is SplashState.Registering -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF4ADE80))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Creando tu perfil...", color = Color(0xFF888888))
                    }
                }

                is SplashState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentState.message, color = Color(0xFFFF6B6B))
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetToInput() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
                        ) {
                            Text("Intentar de nuevo", color = Color.Black)
                        }
                    }
                }

                is SplashState.Success -> {
                    // Vacío, el LaunchedEffect se encarga de cambiar de pantalla
                }
            }
        }
    }
}