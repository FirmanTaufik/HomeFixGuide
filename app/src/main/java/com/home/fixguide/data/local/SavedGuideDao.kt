package com.home.fixguide.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedGuideDao {

    @Query("SELECT * FROM saved_guides ORDER BY savedAt DESC")
    fun getAllSavedGuides(): Flow<List<SavedGuideEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_guides WHERE id = :id)")
    fun isSaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSavedGuide(guide: SavedGuideEntity)

    @Query("DELETE FROM saved_guides WHERE id = :id")
    fun deleteSavedGuideById(id: String)
}
