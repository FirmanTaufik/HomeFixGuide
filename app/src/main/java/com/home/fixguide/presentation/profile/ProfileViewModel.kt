package com.home.fixguide.presentation.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.fixguide.data.local.ThemeManager
import com.home.fixguide.data.model.AuthUser
import com.home.fixguide.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val themeManager: ThemeManager
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser
    val isDarkMode: StateFlow<Boolean?> = themeManager.isDarkMode

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    fun onGoogleIdTokenReceived(
        idToken: String?,
        email: String?,
        displayName: String?,
        photoUrl: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            if (!idToken.isNullOrEmpty()) {
                val result = authRepository.handleGoogleSignInToken(idToken)
                if (result.isFailure) {
                    val user = AuthUser(
                        id = email ?: "google_user_${System.currentTimeMillis()}",
                        displayName = displayName ?: "Google User",
                        email = email,
                        photoUrl = photoUrl
                    )
                    authRepository.saveUserSession(user)
                }
                _message.value = "Successfully signed in with Google!"
            } else if (!email.isNullOrEmpty() || !displayName.isNullOrEmpty()) {
                val user = AuthUser(
                    id = email ?: "google_user_${System.currentTimeMillis()}",
                    displayName = displayName ?: "Google User",
                    email = email,
                    photoUrl = photoUrl
                )
                authRepository.saveUserSession(user)
                _message.value = "Successfully signed in with Google!"
            } else {
                _message.value = "Failed to process Google sign-in data."
            }
            _isLoading.value = false
        }
    }

    fun loginDemoUser() {
        viewModelScope.launch {
            val user = AuthUser(
                id = "demo_user_123",
                displayName = "Google Demo User",
                email = "user.demo@gmail.com",
                photoUrl = null
            )
            authRepository.saveUserSession(user)
            _message.value = "Signed in as Demo User!"
        }
    }

    fun onGoogleSignInError(error: String) {
        _message.value = "Google Sign-In error: $error"
    }

    fun clearMessage() {
        _message.value = null
    }

    fun promptLogout() {
        _showLogoutDialog.value = true
    }

    fun dismissLogoutDialog() {
        _showLogoutDialog.value = false
    }

    fun confirmLogout() {
        _showLogoutDialog.value = false
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signOut()
            _isLoading.value = false
            _message.value = "Successfully logged out."
        }
    }

    fun openPrivacyPolicy(context: Context) {
        val privacyUrl = "https://homefixguide.pages.dev/privacy"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            _message.value = "Unable to open Privacy Policy link"
        }
    }

    fun openPlayStore(context: Context) {
        val packageName = context.packageName
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(marketIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                _message.value = "Unable to open Play Store"
            }
        }
    }
}
