package com.example.vkcourse.applist.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface AppDetailsDao {
    @Query("SELECT * FROM app_details WHERE id = :id")
    fun getAppDetails(id: String): Flow<AppDetailsEntity?>

    @Upsert
    suspend fun upsertAppDetails(entity: AppDetailsEntity)

    @Query("UPDATE app_details SET isInWishlist = :isInWishlist WHERE id = :id")
    suspend fun updateWishlistStatus(id: String, isInWishlist: Boolean)

    // Обновление с сервера не должно затирать локальный выбор пользователя.
    @Transaction
    suspend fun saveRemoteDetails(entity: AppDetailsEntity): AppDetailsEntity {
        val saved = entity.copy(isInWishlist = getAppDetails(entity.id).first()?.isInWishlist ?: false)
        upsertAppDetails(saved)
        return saved
    }

    // Чтение и запись в одной транзакции сохраняют результат быстрых повторных нажатий.
    @Transaction
    suspend fun toggleWishlist(id: String) {
        getAppDetails(id).first()?.let { updateWishlistStatus(id, !it.isInWishlist) }
    }
}
