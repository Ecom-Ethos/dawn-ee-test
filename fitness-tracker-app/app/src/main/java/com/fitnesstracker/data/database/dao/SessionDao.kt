package com.fitnesstracker.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fitnesstracker.data.database.entities.Session

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions WHERE clientId = :clientId ORDER BY date DESC")
    fun getSessionsForClient(clientId: Long): LiveData<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): Session?

    @Query("SELECT * FROM sessions WHERE clientId = :clientId ORDER BY date DESC LIMIT 1")
    suspend fun getLastSession(clientId: Long): Session?

    @Query("SELECT COUNT(*) FROM sessions WHERE clientId = :clientId")
    fun getSessionCountForClient(clientId: Long): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)
}
