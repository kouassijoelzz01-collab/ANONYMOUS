package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvatarCatalog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        when (uiState) {
            is AuthUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Chargement de Game Zone 2...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            is AuthUiState.EmailVerificationPending -> {
                EmailVerificationPendingView(
                    email = uiState.email,
                    pseudo = uiState.pseudo,
                    onCheckVerification = { onResult ->
                        viewModel.checkEmailVerification(onResult)
                    },
                    onResend = { onResult ->
                        viewModel.resendVerificationEmail(onResult)
                    },
                    onProceed = {
                        viewModel.proceedToApp()
                    },
                    onCancel = {
                        viewModel.signOut()
                    }
                )
            }

            is AuthUiState.ProfileSetupRequired -> {
                ProfileSetupView(
                    email = uiState.email,
                    isChecking = false,
                    onComplete = { pseudo, avatarId, onResult ->
                        viewModel.completeProfile(pseudo, avatarId, onResult)
                    }
                )
            }

            is AuthUiState.Unauthenticated, is AuthUiState.Error -> {
                val errorMessage = (uiState as? AuthUiState.Error)?.message
                var showGuestDialog by remember { mutableStateOf(false) }
                var showEmailRegisterDialog by remember { mutableStateOf(false) }
                var showEmailSignInDialog by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo Icon with glowing ring
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(NeonPurple.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(listOf(NeonPurple, NeonCyan)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Game Zone 2",
                            tint = NeonCyan,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "GAME ZONE 2",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "L'ARÈNE DES JEUX 1V1 EN TEMPS RÉEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Feature highlights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FeatureChip("6 Jeux 1v1", "🎮")
                        FeatureChip("Matchmaking", "⚡")
                        FeatureChip("Salons Privés", "👥")
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Error display if any
                    AnimatedVisibility(visible = errorMessage != null) {
                        if (errorMessage != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF3B151E)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = NeonPink,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage,
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // 1. Google Sign-In Button
                    Button(
                        onClick = { viewModel.signInWithGoogle(context) },
                        modifier = Modifier
                            .testTag("google_sign_in_button")
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SE CONNECTER AVEC GOOGLE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Email / Gmail Registration Button
                    Button(
                        onClick = { showEmailRegisterDialog = true },
                        modifier = Modifier
                            .testTag("email_register_button")
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "S'INSCRIRE AVEC UN GMAIL / EMAIL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Guest Login Button
                    OutlinedButton(
                        onClick = { showGuestDialog = true },
                        modifier = Modifier
                            .testTag("guest_login_button")
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🎭", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "JOUER EN TANT QU'INVITÉ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Already have an email account?
                    TextButton(onClick = { showEmailSignInDialog = true }) {
                        Text(
                            text = "Déjà un compte ? Se connecter par email",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Dialog: Email Registration with Gmail confirmation
                    if (showEmailRegisterDialog) {
                        EmailRegisterDialog(
                            onDismiss = { showEmailRegisterDialog = false },
                            onRegister = { email, password, pseudo, avatarId, onResult ->
                                viewModel.registerWithEmail(email, password, pseudo, avatarId, onResult)
                            }
                        )
                    }

                    // Dialog: Email Sign In
                    if (showEmailSignInDialog) {
                        EmailSignInDialog(
                            onDismiss = { showEmailSignInDialog = false },
                            onSignIn = { email, password, onResult ->
                                viewModel.signInWithEmail(email, password, onResult)
                            }
                        )
                    }

                    // Dialog: Guest Mode
                    if (showGuestDialog) {
                        GuestSetupDialog(
                            onDismiss = { showGuestDialog = false },
                            onConfirm = { guestPseudo, avatarId ->
                                showGuestDialog = false
                                viewModel.signInAsGuest(guestPseudo, avatarId)
                            }
                        )
                    }
                }
            }

            is AuthUiState.Authenticated -> {
                // Handled in root navigation
            }
        }
    }
}

/**
 * Écran d'attente de validation de l'email (Gmail)
 */
@Composable
fun EmailVerificationPendingView(
    email: String,
    pseudo: String,
    onCheckVerification: ((Boolean) -> Unit) -> Unit,
    onResend: ((Boolean, String?) -> Unit) -> Unit,
    onProceed: () -> Unit,
    onCancel: () -> Unit
) {
    var checkStatusMessage by remember { mutableStateOf<String?>(null) }
    var isChecking by remember { mutableStateOf(false) }
    var resendSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing Mail Icon
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(2.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MarkEmailRead,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "VÉRIFIE TA BOÎTE GMAIL !",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenue dans l'arène, $pseudo !",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NeonPurple
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Un message de validation a été envoyé à :",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clique sur le lien reçu dans ton email pour confirmer ton compte et débloquer toutes les fonctionnalités.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (checkStatusMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = checkStatusMessage ?: "",
                color = if (resendSuccess) NeonGreen else NeonPink,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button 1: J'ai vérifié mon email
        Button(
            onClick = {
                isChecking = true
                checkStatusMessage = null
                onCheckVerification { verified ->
                    isChecking = false
                    if (verified) {
                        checkStatusMessage = "Email vérifié avec succès !"
                    } else {
                        checkStatusMessage = "Email pas encore validé. Pense à vérifier tes courriers indésirables / spams."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
            if (isChecking) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "J'AI CLIQUÉ SUR LE LIEN (VÉRIFIER)", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Button 2: Renvoyer l'email
        OutlinedButton(
            onClick = {
                onResend { success, err ->
                    resendSuccess = success
                    checkStatusMessage = if (success) "Nouveau message de validation envoyé !" else (err ?: "Erreur d'envoi")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Renvoyer l'email de validation", color = NeonCyan, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Button 3: Continuer quand même vers le jeu
        Button(
            onClick = onProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
        ) {
            Text(text = "Continuer vers le jeu", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(onClick = onCancel) {
            Text(text = "Retour / Changer de compte", color = TextMuted, fontSize = 12.sp)
        }
    }
}

/**
 * Boîte de dialogue d'inscription par Email / Gmail avec mot de passe et pseudo
 */
@Composable
fun EmailRegisterDialog(
    onDismiss: () -> Unit,
    onRegister: (String, String, String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var pseudo by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("cyber_ninja") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Inscription Email / Gmail", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Remplis ce formulaire pour créer ton compte. Un message de validation te sera envoyé.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Avatar Selection
                Text(text = "Choisis ton avatar :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AvatarCatalog.avatars) { av ->
                        val isSel = av.id == selectedAvatarId
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(av.hexColor).copy(alpha = 0.35f) else DarkSurfaceCard)
                                .border(
                                    if (isSel) 2.dp else 1.dp,
                                    if (isSel) Color(av.hexColor) else Color(0xFF2D3748),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedAvatarId = av.id },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = av.emoji, fontSize = 20.sp)
                        }
                    }
                }

                // Pseudo
                OutlinedTextField(
                    value = pseudo,
                    onValueChange = { if (it.length <= 15) pseudo = it },
                    label = { Text("Pseudo joueur (3-15 car.)", fontSize = 12.sp) },
                    placeholder = { Text("Ex: CyberWarrior", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse Gmail / Email", fontSize = 12.sp) },
                    placeholder = { Text("ton.email@gmail.com", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe (min 6 car.)", fontSize = 12.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(text = errorMessage ?: "", color = NeonPink, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    errorMessage = null
                    onRegister(email, password, pseudo, selectedAvatarId) { success, err ->
                        isSubmitting = false
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = err
                        }
                    }
                },
                enabled = !isSubmitting && email.isNotBlank() && password.length >= 6 && pseudo.length >= 3,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("S'INSCRIRE ET RECEVOIR LE MESSAGE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextSecondary)
            }
        }
    )
}

/**
 * Boîte de dialogue de connexion par Email
 */
@Composable
fun EmailSignInDialog(
    onDismiss: () -> Unit,
    onSignIn: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Connexion par Email", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse email", fontSize = 12.sp) },
                    placeholder = { Text("ton.email@gmail.com", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe", fontSize = 12.sp) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(text = errorMessage ?: "", color = NeonPink, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    errorMessage = null
                    onSignIn(email, password) { success, err ->
                        isSubmitting = false
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = err
                        }
                    }
                },
                enabled = !isSubmitting && email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("SE CONNECTER", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextSecondary)
            }
        }
    )
}

@Composable
fun GuestSetupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var guestPseudo by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("neon_cat") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎭", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mode Invité Rapide", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            }
        },
        text = {
            Column {
                Text(
                    text = "Accède immédiatement à tous les jeux. Tu pourras lier un compte Google plus tard.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Choisis un avatar :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AvatarCatalog.avatars) { av ->
                        val isSel = av.id == selectedAvatarId
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) Color(av.hexColor).copy(alpha = 0.35f) else DarkSurfaceCard)
                                .border(
                                    if (isSel) 2.dp else 1.dp,
                                    if (isSel) Color(av.hexColor) else Color(0xFF2D3748),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedAvatarId = av.id },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = av.emoji, fontSize = 22.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Pseudo invité (optionnel) :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = guestPseudo,
                    onValueChange = { if (it.length <= 15) guestPseudo = it },
                    placeholder = { Text("Ex: Invité_${(1000..9999).random()}", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurfaceCard,
                        unfocusedContainerColor = DarkSurfaceCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(guestPseudo, selectedAvatarId) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("JOUER DIRECT", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ProfileSetupView(
    email: String,
    isChecking: Boolean,
    onComplete: (String, String, (Boolean, String?) -> Unit) -> Unit
) {
    var pseudo by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("cyber_ninja") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CRÉATION DU PROFIL",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Personnalise ton identité de joueur pour Game Zone 2",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selected avatar preview
        val currentAvatar = AvatarCatalog.getAvatar(selectedAvatarId)
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(currentAvatar.hexColor).copy(alpha = 0.2f))
                .border(3.dp, Color(currentAvatar.hexColor), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = currentAvatar.emoji, fontSize = 44.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentAvatar.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(currentAvatar.hexColor)
        )
        Text(text = currentAvatar.role, fontSize = 12.sp, color = TextMuted)

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal Avatar Picker
        Text(
            text = "Choisis ton avatar :",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(AvatarCatalog.avatars) { avatar ->
                val isSelected = avatar.id == selectedAvatarId
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) Color(avatar.hexColor).copy(alpha = 0.3f)
                            else DarkSurfaceCard
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(avatar.hexColor) else Color(0xFF2D3748),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedAvatarId = avatar.id },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = avatar.emoji, fontSize = 28.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pseudo input field
        Text(
            text = "Pseudo unique (3-15 caractères) :",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pseudo,
            onValueChange = {
                if (it.length <= 15) {
                    pseudo = it.filter { char -> char.isLetterOrDigit() || char == '_' }
                    errorMsg = null
                }
            },
            placeholder = { Text("Ex: ShadowGamer", color = TextMuted) },
            singleLine = true,
            modifier = Modifier
                .testTag("pseudo_input_field")
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(14.dp),
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (pseudo.length < 3) "Min. 3 caractères" else "Pseudo valide",
                        color = if (pseudo.length >= 3) NeonCyan else TextMuted,
                        fontSize = 11.sp
                    )
                    Text(text = "${pseudo.length}/15", color = TextMuted, fontSize = 11.sp)
                }
            }
        )

        AnimatedVisibility(visible = errorMsg != null) {
            if (errorMsg != null) {
                Text(
                    text = errorMsg ?: "",
                    color = NeonPink,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.Start)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (pseudo.length < 3) {
                    errorMsg = "Le pseudo doit comporter au moins 3 caractères."
                    return@Button
                }
                isSubmitting = true
                errorMsg = null
                onComplete(pseudo, selectedAvatarId) { success, err ->
                    isSubmitting = false
                    if (!success) {
                        errorMsg = err ?: "Erreur d'enregistrement."
                    }
                }
            },
            enabled = !isSubmitting && pseudo.length >= 3,
            modifier = Modifier
                .testTag("submit_profile_button")
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPurple,
                disabledContainerColor = Color(0xFF374151)
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(text = "ENTRER DANS LA ZONE", fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun FeatureChip(label: String, icon: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(1.dp, Color(0xFF2D3748), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}
