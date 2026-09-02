package com.home.fixguide.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.home.fixguide.data.model.AuthUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fixguide_auth_prefs", Context.MODE_PRIVATE)

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _currentUser = MutableStateFlow<AuthUser?>(loadSavedUser())
    override val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private fun loadSavedUser(): AuthUser? {
        // Try Firebase first if user is logged in
        val fbUser = try {
            firebaseAuth.currentUser
        } catch (e: Exception) {
            null
        }

        if (fbUser != null) {
            val user = AuthUser(
                id = fbUser.uid,
                displayName = fbUser.displayName,
                email = fbUser.email,
                photoUrl = fbUser.photoUrl?.toString()
            )
            saveToPrefs(user)
            return user
        }

        // Fallback to SharedPreferences
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val displayName = prefs.getString(KEY_DISPLAY_NAME, null)
        val email = prefs.getString(KEY_EMAIL, null)
        val photoUrl = prefs.getString(KEY_PHOTO_URL, null)

        return AuthUser(
            id = userId,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl
        )
    }

    override fun saveUserSession(user: AuthUser) {
        _currentUser.value = user
        saveToPrefs(user)
    }

    private fun saveToPrefs(user: AuthUser) {
        prefs.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_DISPLAY_NAME, user.displayName)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PHOTO_URL, user.photoUrl)
            .apply()
    }

    override suspend fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (_: Exception) {}

        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    override suspend fun handleGoogleSignInToken(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val fbUser = authResult.user
            if (fbUser != null) {
                val user = AuthUser(
                    id = fbUser.uid,
                    displayName = fbUser.displayName,
                    email = fbUser.email,
                    photoUrl = fbUser.photoUrl?.toString()
                )
                saveUserSession(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to get Firebase User"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_PHOTO_URL = "key_photo_url"
    }
}
