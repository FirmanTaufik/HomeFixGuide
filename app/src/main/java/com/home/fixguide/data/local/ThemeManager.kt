package com.home.fixguide.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fixguide_theme_prefs", Context.MODE_PRIVATE)

    // null = Follow System, true = Dark Mode, false = Light Mode
    private val _isDarkMode = MutableStateFlow<Boolean?>(loadThemePreference())
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private fun loadThemePreference(): Boolean? {
        if (!prefs.contains(KEY_DARK_MODE)) return null
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(enabled: Boolean?) {
        _isDarkMode.value = enabled
        val editor = prefs.edit()
        if (enabled == null) {
            editor.remove(KEY_DARK_MODE)
        } else {
            editor.putBoolean(KEY_DARK_MODE, enabled)
        }
        editor.apply()
    }

    fun toggleDarkMode(currentIsSystemDark: Boolean) {
        val current = _isDarkMode.value ?: currentIsSystemDark
        setDarkMode(!current)
    }

    companion object {
        private const val KEY_DARK_MODE = "key_dark_mode"
    }
}
