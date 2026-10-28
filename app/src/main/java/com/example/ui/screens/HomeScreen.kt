package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AvatarCatalog
import com.example.data.model.GameCatalog
import com.example.data.model.GameInfo
import com.example.data.model.UserProfile
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    profile: UserProfile,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGameForDetails by remember { mutableStateOf<GameInfo?>(null) }
    var showMatchmakingDialog by remember { mutableStateOf(false) }
    var showPrivateRoomDialog by remember { mutableStateOf(false) }
    var selectedGameForAction by remember { mutableStateOf(GameCatalog.games.first()) }
    var roomCodeInput by remember { mutableStateOf("") }
    var generatedRoomCode by remember { mutableStateOf<String?>(null) }

    val avatar = AvatarCatalog.getAvatar(profile.avatarId)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Player Status & Avatar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Glow
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(avatar.hexColor).copy(alpha = 0.2f))
                        .border(2.dp, Color(avatar.hexColor), CircleShape)
                        .clickable { onNavigateToProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = avatar.emoji, fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.pseudo,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        if (profile.isGuest) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF334155))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "INVITÉ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                    Text(
                        text = if (profile.isGuest) "Mode Invité • ${profile.wins} Victoires" else "${avatar.name} • ${profile.wins} Victoires",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Win rate chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceCard)
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${profile.winRate}% WIN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }

        // 2. Hero Banner Illustration
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_gamezone),
                        contentDescription = "Game Zone Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        DarkBackground.copy(alpha = 0.85f),
                                        DarkBackground.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "GAME ZONE 2",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Défiez vos amis ou des inconnus en 1v1",
                            fontSize = 12.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. THE 2 BIG PRIMARY ACTION BUTTONS (Requested by user)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // BUTTON 1: JOUER EN LIGNE
                Button(
                    onClick = { showMatchmakingDialog = true },
                    modifier = Modifier
                        .testTag("play_online_button")
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurple
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🌍 JOUER EN LIGNE",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Adversaire aléatoire • Matchmaking direct",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                // BUTTON 2: JOUER ENTRE AMIS
                Button(
                    onClick = { showPrivateRoomDialog = true },
                    modifier = Modifier
                        .testTag("play_with_friends_button")
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "👥 JOUER ENTRE AMIS",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Salon privé • Code à 5 caractères",
                                    fontSize = 11.sp,
                                    color = Color.Black.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        // 4. Section Title: LES 6 JEUX 1V1
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LES 6 JEUX DISPONIBLES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "1 contre 1",
                    fontSize = 12.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 5. Game Cards for each of the 6 games
        items(GameCatalog.games) { game ->
            GameCard(
                game = game,
                onClick = { selectedGameForDetails = game },
                onQuickPlay = {
                    selectedGameForAction = game
                    showMatchmakingDialog = true
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Modal Bottom Sheet: Game Details & Rules
    selectedGameForDetails?.let { game ->
        ModalBottomSheet(
            onDismissRequest = { selectedGameForDetails = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            GameDetailsSheetContent(
                game = game,
                onClose = { selectedGameForDetails = null },
                onPlayOnline = {
                    selectedGameForAction = game
                    selectedGameForDetails = null
                    showMatchmakingDialog = true
                },
                onPlayFriends = {
                    selectedGameForAction = game
                    selectedGameForDetails = null
                    showPrivateRoomDialog = true
                }
            )
        }
    }

    // Modal Sheet: Matchmaking Launcher
    if (showMatchmakingDialog) {
        ModalBottomSheet(
            onDismissRequest = { showMatchmakingDialog = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            MatchmakingSheetContent(
                selectedGame = selectedGameForAction,
                onGameSelected = { selectedGameForAction = it },
                onClose = { showMatchmakingDialog = false }
            )
        }
    }

    // Modal Sheet: Private Rooms (Create or Join)
    if (showPrivateRoomDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                showPrivateRoomDialog = false
                generatedRoomCode = null
            },
            sheetState = rememberModalBottomSheetState(),
            containerColor = DarkSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            PrivateRoomSheetContent(
                selectedGame = selectedGameForAction,
                onGameSelected = { selectedGameForAction = it },
                roomCodeInput = roomCodeInput,
                onRoomCodeChange = { roomCodeInput = it },
                generatedRoomCode = generatedRoomCode,
                onGenerateCode = {
                    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
                    generatedRoomCode = (1..5).map { chars.random() }.joinToString("")
                },
                onClose = {
                    showPrivateRoomDialog = false
                    generatedRoomCode = null
                }
            )
        }
    }
}

@Composable
fun GameCard(
    game: GameInfo,
    onClick: () -> Unit,
    onQuickPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(game.accentColor).copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = DarkSurfaceCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(game.accentColor).copy(alpha = 0.2f))
                    .border(1.5.dp, Color(game.accentColor).copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = game.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(game.accentColor).copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = game.badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(game.accentColor)
                        )
                    }
                }

                Text(
                    text = game.subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = game.durationText,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Quick Play Button
            Button(
                onClick = onQuickPlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(game.accentColor)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Text(
                    text = "Jouer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun GameDetailsSheetContent(
    game: GameInfo,
    onClose: () -> Unit,
    onPlayOnline: () -> Unit,
    onPlayFriends: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = game.iconEmoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = game.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = game.subtitle,
                        fontSize = 12.sp,
                        color = Color(game.accentColor)
                    )
                }
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = game.description,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rules Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Règles du jeu :",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                game.rules.forEach { rule ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "• ", color = NeonCyan, fontWeight = FontWeight.Bold)
                        Text(text = rule, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
            onClick = onPlayOnline,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
            Text(
                text = "🌍 JOUER EN LIGNE (ADVERSAIRE ALÉATOIRE)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onPlayFriends,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))
            )
        ) {
            Text(
                text = "👥 CRÉER / REJOINDRE UN SALON PRIVÉ",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MatchmakingSheetContent(
    selectedGame: GameInfo,
    onGameSelected: (GameInfo) -> Unit,
    onClose: () -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌍 MATCHMAKING EN LIGNE",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Trouve un adversaire aléatoire pour un duel 1v1",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceVariant)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = selectedGame.iconEmoji, fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recherche d'un adversaire sur ${selectedGame.title}...",
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "File d'attente Firestore en écoute (Étape 3 & 4)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isSearching = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Annuler la recherche")
                    }
                }
            }
        } else {
            Text(
                text = "Choisir le jeu :",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GameCatalog.games.forEach { game ->
                    val isSelected = game.id == selectedGame.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(game.accentColor).copy(alpha = 0.25f) else DarkSurfaceCard)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(game.accentColor) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onGameSelected(game) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = game.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = game.title,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = game.subtitle,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (isSelected) {
                            Text(text = "SÉLECTIONNÉ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(game.accentColor))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { isSearching = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text(
                    text = "LANCER LE MATCHMAKING",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PrivateRoomSheetContent(
    selectedGame: GameInfo,
    onGameSelected: (GameInfo) -> Unit,
    roomCodeInput: String,
    onRoomCodeChange: (String) -> Unit,
    generatedRoomCode: String?,
    onGenerateCode: () -> Unit,
    onClose: () -> Unit
) {
    var mode by remember { mutableStateOf(0) } // 0: Créer, 1: Rejoindre

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👥 SALON PRIVÉ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Joue contre un ami en partageant un code",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs: Créer / Rejoindre
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceCard)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (mode == 0) NeonCyan else Color.Transparent)
                    .clickable { mode = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Créer un salon",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (mode == 0) Color.Black else TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (mode == 1) NeonCyan else Color.Transparent)
                    .clickable { mode = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Rejoindre un salon",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (mode == 1) Color.Black else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mode == 0) {
            // Créer un salon
            Text(
                text = "1. Choisis le jeu : ${selectedGame.title}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (generatedRoomCode == null) {
                Button(
                    onClick = onGenerateCode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("GÉNÉRER LE CODE DU SALON", fontWeight = FontWeight.Bold)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Code du salon :", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = generatedRoomCode,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "Partage ce code à ton ami pour qu'il rejoigne",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            // Rejoindre un salon
            Text(
                text = "Saisis le code à 5 caractères :",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = roomCodeInput,
                onValueChange = {
                    if (it.length <= 5) onRoomCodeChange(it.uppercase())
                },
                placeholder = { Text("Ex: X7K9A", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Will connect in step 4 & 5 */ },
                enabled = roomCodeInput.length == 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text(
                    text = "REJOINDRE LE SALON",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
