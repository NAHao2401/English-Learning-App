package com.example.englishlearningapp.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.englishlearningapp.features.profile.viewmodel.ProfileViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val backgroundColor = if (uiState.isDarkMode) Color(0xFF121212) else Color(0xFFF8F6FF)
    val cardColor = if (uiState.isDarkMode) Color(0xFF1E1E1E) else Color.White
    val primaryTextColor = if (uiState.isDarkMode) Color.White else Color(0xFF1D1B2F)
    val secondaryTextColor = if (uiState.isDarkMode) Color(0xFFB8B8C8) else Color(0xFF77738A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp)
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = primaryTextColor
            )

            Text(
                text = "Manage your account and app settings",
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryTextColor,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileHeaderCard(
                userName = uiState.userName.ifBlank { "Learner" },
                userEmail = uiState.userEmail.ifBlank { "No email" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle(
                title = "Account",
                color = primaryTextColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    InfoRow(
                        icon = Icons.Rounded.Person,
                        title = "Name",
                        value = uiState.userName.ifBlank { "Learner" },
                        iconBackground = Color(0xFFE9E7FF),
                        iconTint = Color(0xFF6C63FF),
                        titleColor = primaryTextColor,
                        valueColor = secondaryTextColor
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = if (uiState.isDarkMode) Color(0xFF2E2E2E) else Color(0xFFEDEAF8)
                    )

                    InfoRow(
                        icon = Icons.Rounded.Email,
                        title = "Email",
                        value = uiState.userEmail.ifBlank { "No email" },
                        iconBackground = Color(0xFFE8F5E9),
                        iconTint = Color(0xFF43A047),
                        titleColor = primaryTextColor,
                        valueColor = secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            SectionTitle(
                title = "Settings",
                color = primaryTextColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    SettingSwitchRow(
                        icon = if (uiState.isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                        title = "Dark Mode",
                        description = if (uiState.isDarkMode) {
                            "Dark theme is enabled"
                        } else {
                            "Light theme is enabled"
                        },
                        checked = uiState.isDarkMode,
                        onCheckedChange = viewModel::setDarkMode,
                        iconBackground = Color(0xFFE9E7FF),
                        iconTint = Color(0xFF6C63FF),
                        titleColor = primaryTextColor,
                        descriptionColor = secondaryTextColor
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = if (uiState.isDarkMode) Color(0xFF2E2E2E) else Color(0xFFEDEAF8)
                    )

                    SettingSwitchRow(
                        icon = if (uiState.isSoundEnabled) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff,
                        title = "Sound",
                        description = if (uiState.isSoundEnabled) {
                            "Vocabulary sound is enabled"
                        } else {
                            "Vocabulary sound is disabled"
                        },
                        checked = uiState.isSoundEnabled,
                        onCheckedChange = viewModel::setSoundEnabled,
                        iconBackground = Color(0xFFFFF3E0),
                        iconTint = Color(0xFFFF9800),
                        titleColor = primaryTextColor,
                        descriptionColor = secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showChangePasswordDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isDarkMode) {
                        Color(0xFF2A253A)
                    } else {
                        Color(0xFFEDE9FF)
                    },
                    contentColor = Color(0xFF6C63FF)
                )
            ) {
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Change Password",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEBEE),
                    contentColor = Color(0xFFE53935)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = "Logout"
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = null,
                    tint = Color(0xFFE53935)
                )
            },
            title = {
                Text(
                    text = "Logout?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout from your account?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onLogoutSuccess)
                    }
                ) {
                    Text(
                        text = "Logout",
                        color = Color(0xFFE53935),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isLoading = uiState.isChangingPassword,
            onDismiss = { showChangePasswordDialog = false },
            onChangePassword = { currentPassword, newPassword, confirmPassword, onResult ->
                viewModel.changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    onResult = onResult
                )
            }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    userName: String,
    userEmail: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF6C63FF),
                            Color(0xFF8E7CFF),
                            Color(0xFFFF8A65)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Profile avatar",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Settings,
            contentDescription = null,
            tint = Color(0xFF6C63FF),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    title: String,
    value: String,
    iconBackground: Color,
    iconTint: Color,
    titleColor: Color,
    valueColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconBackground: Color,
    iconTint: Color,
    titleColor: Color,
    descriptionColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = descriptionColor
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6C63FF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD8D4E8)
            )
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onChangePassword: (
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
        onResult: (Boolean, String) -> Unit
    ) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val isFormValid = currentPassword.isNotBlank() &&
            newPassword.isNotBlank() &&
            confirmPassword.isNotBlank()

    Dialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Change Password",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Keep your account secure",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                PasswordTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        message = null
                    },
                    label = "Current password",
                    visible = currentPasswordVisible,
                    onToggleVisibility = {
                        currentPasswordVisible = !currentPasswordVisible
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        message = null
                    },
                    label = "New password",
                    visible = newPasswordVisible,
                    onToggleVisibility = {
                        newPasswordVisible = !newPasswordVisible
                    },
                    supportingText = "At least 6 characters"
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        message = null
                    },
                    label = "Confirm password",
                    visible = confirmPasswordVisible,
                    onToggleVisibility = {
                        confirmPasswordVisible = !confirmPasswordVisible
                    },
                    isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                    supportingText = if (
                        confirmPassword.isNotEmpty() && newPassword != confirmPassword
                    ) {
                        "Password does not match"
                    } else {
                        null
                    }
                )

                if (message != null) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSuccess) {
                                    Color(0xFFE8F5E9)
                                } else {
                                    Color(0xFFFFEBEE)
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) {
                                Icons.Rounded.CheckCircle
                            } else {
                                Icons.Rounded.Error
                            },
                            contentDescription = null,
                            tint = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = message.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            onChangePassword(
                                currentPassword,
                                newPassword,
                                confirmPassword
                            ) { success, resultMessage ->
                                isSuccess = success
                                message = resultMessage

                                if (success) {
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                }
                            }
                        },
                        enabled = !isLoading && isFormValid,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Save",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp),
        singleLine = true,
        placeholder = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onToggleVisibility,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (visible) {
                        Icons.Rounded.VisibilityOff
                    } else {
                        Icons.Rounded.Visibility
                    },
                    contentDescription = if (visible) {
                        "Hide password"
                    } else {
                        "Show password"
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        isError = isError,
        supportingText = {
            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}
