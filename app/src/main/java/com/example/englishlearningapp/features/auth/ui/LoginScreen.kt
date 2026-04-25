package com.example.englishlearningapp.features.auth.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.englishlearningapp.features.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    val pageBackground = Brush.verticalGradient(
        colors = listOf(
            colorScheme.surface.copy(alpha = 0.98f),
            Color(0xFFEAF3FF),
            Color(0xFFDCE9FF)
        )
    )
    val cardBackground = Color.White.copy(alpha = 0.95f)
    val primaryText = Color(0xFF22345D)
    val secondaryText = Color(0xFF6A7898)
    val subtleStroke = Color(0xFFD8E5FF)
    val fieldBackground = Color(0xFFF7FAFF)
    val accentBlue = colorScheme.primary

    var passwordVisible by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(uiState.isRegisterSuccess) {
        if(uiState.isRegisterSuccess){
            showSuccessDialog = true
        }
    }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground)
    ) {
        DecorativeOrb(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 42.dp, y = (-28).dp)
                .size(240.dp),
            color = Color(0xFFBFD4FF),
            alpha = 0.75f
        )
        DecorativeOrb(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-72).dp, y = (-40).dp)
                .size(180.dp),
            color = Color(0xFFCBEFEA),
            alpha = 0.5f
        )
        DecorativeOrb(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 48.dp, y = 12.dp)
                .size(164.dp),
            color = Color(0xFFFFE2C7),
            alpha = 0.45f
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 20.dp, end = 28.dp)
                .size(88.dp)
                .border(1.dp, Color.White.copy(alpha = 0.75f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 14.dp)
                    .size(18.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .widthIn(max = 420.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(38.dp),
                    color = Color(0xFFDDE8FF).copy(alpha = 0.72f),
                    shadowElevation = 0.dp
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(716.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth()
                        .shadow(
                            elevation = 26.dp,
                            shape = RoundedCornerShape(34.dp),
                            ambientColor = accentBlue.copy(alpha = 0.18f),
                            spotColor = accentBlue.copy(alpha = 0.18f)
                        ),
                    shape = RoundedCornerShape(34.dp),
                    color = cardBackground,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        BrandHeader(
                            accentBlue = accentBlue,
                            primaryText = primaryText,
                            secondaryText = secondaryText
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Welcome back",
                                color = primaryText,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 34.sp,
                                    lineHeight = 40.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Continue learning English with\nshort lessons, gentle practice,\nand steady progress.",
                                color = secondaryText,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            AuthTextField(
                                label = "Email",
                                value = email,
                                onValueChange = { email = it },
                                trailingContent = {
                                    Text(
                                        text = "@",
                                        color = accentBlue,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                primaryText = primaryText,
                                secondaryText = secondaryText,
                                fieldBackground = fieldBackground,
                                strokeColor = subtleStroke
                            )

                            AuthTextField(
                                label = "Password",
                                value = password,
                                onValueChange = { password = it },
                                visualTransformation = if (passwordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                                trailingContent = {
                                    Text(
                                        text = if (passwordVisible) "Hide" else "Show",
                                        color = accentBlue,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable {
                                            passwordVisible = !passwordVisible
                                        }
                                    )
                                },
                                primaryText = primaryText,
                                secondaryText = secondaryText,
                                fieldBackground = fieldBackground,
                                strokeColor = subtleStroke
                            )

                            Text(
                                text = "Forgot password?",
                                color = accentBlue,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )

                            if (uiState.errorMessage != null) {
                                Text(
                                    text = uiState.errorMessage.orEmpty(),
                                    color = colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(
                                onClick = { viewModel.login(email, password) },
                                enabled = !uiState.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = accentBlue,
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp,
                                    disabledElevation = 0.dp
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )
                                } else {
                                    Text(
                                        text = "Continue",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        OrContinueWithDivider(
                            textColor = secondaryText,
                            strokeColor = subtleStroke
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                            SocialLoginButton(
                                label = "Continue with Google",
                                iconText = "G",
                                iconContainerColor = accentBlue,
                                primaryText = primaryText,
                                strokeColor = Color(0xFFE4EDFF),
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Google login is coming soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )

                            SocialLoginButton(
                                label = "Continue with Apple",
                                iconText = "A",
                                iconContainerColor = primaryText,
                                primaryText = primaryText,
                                strokeColor = Color(0xFFE4EDFF),
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Apple login is coming soon",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }

                        SignUpPrompt(
                            onNavigateToRegister = {
                                viewModel.clearError()
                                onNavigateToRegister()
                            },
                            primaryText = secondaryText,
                            accentBlue = accentBlue
                        )
                    }
                }
            }
        }
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text("Thông báo")
                },
                text = {
                    Text("Đăng ký thành công!")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun BrandHeader(
    accentBlue: Color,
    primaryText: Color,
    secondaryText: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = accentBlue.copy(alpha = 0.12f),
                    spotColor = accentBlue.copy(alpha = 0.12f)
                )
                .background(Color(0xFFF1F6FF), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFD9E7FF), RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .background(accentBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EN",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = 10.dp)
                    .size(10.dp)
                    .background(Color(0xFF98B8FF), CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 18.dp, y = (-14).dp)
                    .size(8.dp)
                    .background(Color(0xFFD6E5FF), CircleShape)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "English Learning App",
                color = primaryText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Calm daily English practice",
                color = secondaryText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    primaryText: Color,
    secondaryText: Color,
    fieldBackground: Color,
    strokeColor: Color,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            color = primaryText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = fieldBackground,
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, strokeColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = primaryText,
                        fontSize = 16.sp
                    ),
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = if (label == "Email") "name@email.com" else "••••••••",
                                color = secondaryText.copy(alpha = 0.65f),
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                            )
                        }
                        innerTextField()
                    }
                )

                if (trailingContent != null) {
                    Spacer(modifier = Modifier.size(12.dp))
                    trailingContent()
                }
            }
        }
    }
}

@Composable
private fun OrContinueWithDivider(
    textColor: Color,
    strokeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DividerLine(
            modifier = Modifier.weight(1f),
            color = strokeColor
        )
        Text(
            text = "or continue with",
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        )
        DividerLine(
            modifier = Modifier.weight(1f),
            color = strokeColor
        )
    }
}

@Composable
private fun DividerLine(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier = modifier
            .height(1.dp)
            .background(color)
    )
}

@Composable
private fun SocialLoginButton(
    label: String,
    iconText: String,
    iconContainerColor: Color,
    primaryText: Color,
    strokeColor: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, strokeColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(iconContainerColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                color = primaryText
            )
        }
    }
}

@Composable
private fun SignUpPrompt(
    onNavigateToRegister: () -> Unit,
    primaryText: Color,
    accentBlue: Color
) {
    val promptText = buildAnnotatedString {
        append("Don’t have an account? ")
        withStyle(SpanStyle(color = accentBlue, fontWeight = FontWeight.SemiBold)) {
            append("Sign up")
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = promptText,
            color = primaryText,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
            modifier = Modifier.clickable(onClick = onNavigateToRegister)
        )
    }
}

@Composable
private fun DecorativeOrb(
    modifier: Modifier,
    color: Color,
    alpha: Float,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = alpha))
    )
}