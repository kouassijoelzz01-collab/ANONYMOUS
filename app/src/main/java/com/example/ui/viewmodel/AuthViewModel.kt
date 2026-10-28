package com.example.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object Unauthenticated : AuthUiState
    data class ProfileSetupRequired(val uid: String, val email: String) : AuthUiState
    data class EmailVerificationPending(val email: String, val pseudo: String) : AuthUiState
    data class Authenticated(val profile: UserProfile) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _isCheckingPseudo = MutableStateFlow(false)
    val isCheckingPseudo: StateFlow<Boolean> = _isCheckingPseudo.asStateFlow()

    private var profileJob: Job? = null
    private var guestProfile: UserProfile? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            guestProfile = null
            // If in pending verification state, do not immediately force observe unless verified
            if (_uiState.value !is AuthUiState.EmailVerificationPending) {
                observeUserProfile(user)
            }
        } else if (guestProfile != null) {
            _uiState.value = AuthUiState.Authenticated(guestProfile!!)
        } else {
            profileJob?.cancel()
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        val current = auth.currentUser
        if (current != null) {
            observeUserProfile(current)
        } else {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    private fun observeUserProfile(user: FirebaseUser) {
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            userRepository.observeProfile(user.uid)
                .catch { error ->
                    Log.w(TAG, "Error observing user profile", error)
                    _uiState.value = AuthUiState.Error("Impossible de charger le profil : ${error.localizedMessage}")
                }
                .collect { profile ->
                    if (profile == null || profile.pseudo.isBlank()) {
                        _uiState.value = AuthUiState.ProfileSetupRequired(
                            uid = user.uid,
                            email = user.email ?: ""
                        )
                    } else {
                        _uiState.value = AuthUiState.Authenticated(profile)
                    }
                }
        }
    }

    /**
     * Inscription par Email / Gmail avec envoi de message de validation
     */
    fun registerWithEmail(
        email: String,
        password: String,
        pseudo: String,
        avatarId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val trimmedEmail = email.trim()
        val trimmedPseudo = pseudo.trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            onResult(false, "Veuillez saisir une adresse email valide.")
            return
        }

        if (password.length < 6) {
            onResult(false, "Le mot de passe doit comporter au moins 6 caractères.")
            return
        }

        if (trimmedPseudo.length < 3 || trimmedPseudo.length > 15) {
            onResult(false, "Le pseudo doit comporter entre 3 et 15 caractères.")
            return
        }

        viewModelScope.launch {
            _isCheckingPseudo.value = true
            // Check pseudo uniqueness
            val available = userRepository.isPseudoAvailable(trimmedPseudo, "")
            if (!available) {
                _isCheckingPseudo.value = false
                onResult(false, "Ce pseudo est déjà pris par un autre joueur.")
                return@launch
            }

            try {
                _uiState.value = AuthUiState.Loading
                // 1. Create User in Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
                val user = authResult.user

                if (user != null) {
                    // 2. Send Verification Email
                    try {
                        user.sendEmailVerification().await()
                        Log.d(TAG, "Verification email sent to $trimmedEmail")
                    } catch (e: Exception) {
                        Log.w(TAG, "Email verification send warning", e)
                    }

                    // 3. Create initial profile in Firestore
                    userRepository.createOrUpdateProfile(
                        uid = user.uid,
                        email = trimmedEmail,
                        pseudo = trimmedPseudo,
                        avatarId = avatarId
                    )

                    _isCheckingPseudo.value = false
                    _uiState.value = AuthUiState.EmailVerificationPending(trimmedEmail, trimmedPseudo)
                    onResult(true, null)
                } else {
                    _isCheckingPseudo.value = false
                    _uiState.value = AuthUiState.Unauthenticated
                    onResult(false, "Impossible de créer le compte utilisateur.")
                }
            } catch (e: FirebaseAuthException) {
                _isCheckingPseudo.value = false
                _uiState.value = AuthUiState.Unauthenticated
                val msg = when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Cette adresse email est déjà associée à un compte existant."
                    "ERROR_INVALID_EMAIL" -> "Format d'adresse email invalide."
                    "ERROR_WEAK_PASSWORD" -> "Le mot de passe est trop faible (minimum 6 caractères)."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "L'inscription par email doit être activée dans la console Firebase. Utilisez Google Sign-In ou le Mode Invité."
                    else -> e.localizedMessage ?: "Erreur d'authentification."
                }
                onResult(false, msg)
            } catch (e: Exception) {
                _isCheckingPseudo.value = false
                _uiState.value = AuthUiState.Unauthenticated
                val errorMsg = e.localizedMessage ?: "Erreur inattendue"
                if (errorMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true)) {
                    onResult(false, "Le fournisseur Email nécessite d'être activé dans la console Firebase. Vous pouvez vous connecter avec Google ou en Mode Invité.")
                } else {
                    onResult(false, errorMsg)
                }
            }
        }
    }

    /**
     * Connexion classique par Email et mot de passe
     */
    fun signInWithEmail(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            onResult(false, "Veuillez renseigner votre email et votre mot de passe.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                auth.signInWithEmailAndPassword(trimmedEmail, password).await()
                onResult(true, null)
            } catch (e: FirebaseAuthException) {
                _uiState.value = AuthUiState.Unauthenticated
                val msg = when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "Aucun compte trouvé avec cet email."
                    "ERROR_WRONG_PASSWORD" -> "Mot de passe incorrect."
                    "ERROR_INVALID_EMAIL" -> "Adresse email invalide."
                    "ERROR_USER_DISABLED" -> "Ce compte a été désactivé."
                    else -> e.localizedMessage ?: "Échec de connexion."
                }
                onResult(false, msg)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Unauthenticated
                onResult(false, e.localizedMessage ?: "Erreur de connexion.")
            }
        }
    }

    /**
     * Vérifier si l'utilisateur a cliqué sur le lien de validation reçu par email
     */
    fun checkEmailVerification(onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                user.reload().await()
                if (user.isEmailVerified) {
                    observeUserProfile(user)
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to reload user", e)
                onResult(false)
            }
        }
    }

    /**
     * Renvoyer le message de validation
     */
    fun resendVerificationEmail(onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "Utilisateur introuvable.")
            return
        }

        viewModelScope.launch {
            try {
                user.sendEmailVerification().await()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Échec d'envoi de l'email.")
            }
        }
    }

    /**
     * Permet d'accéder au jeu directement après avoir envoyé l'email
     */
    fun proceedToApp() {
        val user = auth.currentUser
        if (user != null) {
            observeUserProfile(user)
        } else {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    /**
     * Mode Invité : Permet d'entrer directement dans Game Zone 2 sans connexion
     */
    fun signInAsGuest(customPseudo: String? = null, avatarId: String? = null) {
        profileJob?.cancel()
        val randomNum = (1000..9999).random()
        val pseudo = customPseudo?.trim()?.takeIf { it.isNotBlank() } ?: "Invité_$randomNum"
        val chosenAvatar = avatarId ?: "neon_cat"

        val guest = UserProfile(
            uid = "guest_$randomNum",
            email = "",
            pseudo = pseudo,
            avatarId = chosenAvatar,
            wins = 0L,
            losses = 0L,
            gamesPlayed = 0L,
            isGuest = true
        )
        guestProfile = guest
        _uiState.value = AuthUiState.Authenticated(guest)
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val credentialManager = CredentialManager.create(context)
                val serverClientId = context.getString(R.string.default_web_client_id)

                val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(authCredential).await()
                } else {
                    _uiState.value = AuthUiState.Error("Type de credential inattendu.")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w(TAG, "Connexion Google annulée par l'utilisateur", e)
                if (guestProfile != null) {
                    _uiState.value = AuthUiState.Authenticated(guestProfile!!)
                } else {
                    _uiState.value = AuthUiState.Unauthenticated
                }
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Erreur GetCredentialException", e)
                _uiState.value = AuthUiState.Error("Échec de connexion Google : ${e.localizedMessage}")
            } catch (e: Exception) {
                Log.e(TAG, "Erreur inattendue lors de la connexion", e)
                _uiState.value = AuthUiState.Error("Erreur : ${e.localizedMessage ?: "Inconnue"}")
            }
        }
    }

    fun completeProfile(pseudo: String, avatarId: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "Session expirée. Veuillez vous reconnecter.")
            return
        }

        val trimmed = pseudo.trim()
        if (trimmed.length < 3 || trimmed.length > 15) {
            onResult(false, "Le pseudo doit comporter entre 3 et 15 caractères.")
            return
        }

        viewModelScope.launch {
            _isCheckingPseudo.value = true
            val available = userRepository.isPseudoAvailable(trimmed, user.uid)
            if (!available) {
                _isCheckingPseudo.value = false
                onResult(false, "Ce pseudo est déjà pris. Choisissez-en un autre !")
                return@launch
            }

            val result = userRepository.createOrUpdateProfile(
                uid = user.uid,
                email = user.email ?: "",
                pseudo = trimmed,
                avatarId = avatarId
            )
            _isCheckingPseudo.value = false

            if (result.isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.localizedMessage ?: "Erreur d'enregistrement")
            }
        }
    }

    fun updateAvatar(avatarId: String, onResult: (Boolean) -> Unit) {
        val current = (uiState.value as? AuthUiState.Authenticated)?.profile
        if (current?.isGuest == true) {
            val updated = current.copy(avatarId = avatarId)
            guestProfile = updated
            _uiState.value = AuthUiState.Authenticated(updated)
            onResult(true)
            return
        }

        val user = auth.currentUser ?: return
        viewModelScope.launch {
            val result = userRepository.updateAvatar(user.uid, avatarId)
            onResult(result.isSuccess)
        }
    }

    fun updatePseudo(newPseudo: String, onResult: (Boolean, String?) -> Unit) {
        val current = (uiState.value as? AuthUiState.Authenticated)?.profile ?: return
        val trimmed = newPseudo.trim()
        if (trimmed.length < 3 || trimmed.length > 15) {
            onResult(false, "Le pseudo doit contenir entre 3 et 15 caractères.")
            return
        }

        if (current.isGuest) {
            val updated = current.copy(pseudo = trimmed)
            guestProfile = updated
            _uiState.value = AuthUiState.Authenticated(updated)
            onResult(true, null)
            return
        }

        val user = auth.currentUser ?: return
        viewModelScope.launch {
            _isCheckingPseudo.value = true
            val available = userRepository.isPseudoAvailable(trimmed, user.uid)
            if (!available) {
                _isCheckingPseudo.value = false
                onResult(false, "Ce pseudo est déjà utilisé.")
                return@launch
            }

            val res = userRepository.createOrUpdateProfile(
                uid = user.uid,
                email = user.email ?: "",
                pseudo = trimmed,
                avatarId = current.avatarId
            )
            _isCheckingPseudo.value = false
            onResult(res.isSuccess, res.exceptionOrNull()?.localizedMessage)
        }
    }

    fun signOut() {
        guestProfile = null
        profileJob?.cancel()
        auth.signOut()
        _uiState.value = AuthUiState.Unauthenticated
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
        profileJob?.cancel()
    }
}
