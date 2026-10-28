package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.UserRepository
import com.example.ui.components.GameZoneBottomBar
import com.example.ui.components.NavigationTab
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GameZoneTheme
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val userRepository by lazy {
        UserRepository(applicationContext)
    }

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(userRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GameZoneTheme {
                GameZoneApp(
                    authViewModel = authViewModel,
                    userRepository = userRepository
                )
            }
        }
    }
}

@Composable
fun GameZoneApp(
    authViewModel: AuthViewModel,
    userRepository: UserRepository
) {
    val uiState by authViewModel.uiState.collectAsState()
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    when (val state = uiState) {
        is AuthUiState.Authenticated -> {
            val profile = state.profile

            BackHandler(enabled = currentTab != NavigationTab.HOME) {
                currentTab = NavigationTab.HOME
            }

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentWindowInsets = WindowInsets.statusBars,
                bottomBar = {
                    GameZoneBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        NavigationTab.HOME -> {
                            HomeScreen(
                                profile = profile,
                                onNavigateToProfile = { currentTab = NavigationTab.PROFILE }
                            )
                        }

                        NavigationTab.FRIENDS -> {
                            FriendsScreen(
                                currentProfile = profile,
                                userRepository = userRepository
                            )
                        }

                        NavigationTab.PROFILE -> {
                            ProfileScreen(
                                profile = profile,
                                viewModel = authViewModel
                            )
                        }
                    }
                }
            }
        }

        else -> {
            AuthScreen(
                viewModel = authViewModel,
                uiState = state
            )
        }
    }
}
