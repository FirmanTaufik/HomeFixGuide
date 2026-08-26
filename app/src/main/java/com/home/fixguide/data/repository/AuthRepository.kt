package com.home.fixguide.data.repository

import com.home.fixguide.data.model.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<AuthUser?>
    fun saveUserSession(user: AuthUser)
    suspend fun signOut()
    suspend fun handleGoogleSignInToken(idToken: String): Result<AuthUser>
}
