package com.home.fixguide.data.repository

import com.home.fixguide.data.local.SavedGuideDao
import com.home.fixguide.data.local.SavedGuideEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedGuideRepository @Inject constructor(
    private val savedGuideDao: SavedGuideDao
) {
    fun getAllSavedGuides(): Flow<List<SavedGuideEntity>> {
        return savedGuideDao.getAllSavedGuides()
    }

    fun isSaved(url: String): Flow<Boolean> {
        return savedGuideDao.isSaved(url)
    }

    suspend fun saveGuide(guide: SavedGuideEntity) = withContext(Dispatchers.IO) {
        savedGuideDao.insertSavedGuide(guide)
    }

    suspend fun removeGuide(url: String) = withContext(Dispatchers.IO) {
        savedGuideDao.deleteSavedGuideById(url)
    }

    suspend fun toggleSave(
        url: String,
        title: String,
        imageUrl: String = "",
        categoryType: String = "guide",
        currentSavedStatus: Boolean
    ) {
        if (currentSavedStatus) {
            removeGuide(url)
        } else {
            saveGuide(
                SavedGuideEntity(
                    id = url,
                    title = title,
                    imageUrl = imageUrl,
                    url = url,
                    categoryType = categoryType
                )
            )
        }
    }
}
