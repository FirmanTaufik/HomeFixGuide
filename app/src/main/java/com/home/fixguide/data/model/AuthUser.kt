package com.home.fixguide.data.model

data class AuthUser(
    val id: String,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null
)
