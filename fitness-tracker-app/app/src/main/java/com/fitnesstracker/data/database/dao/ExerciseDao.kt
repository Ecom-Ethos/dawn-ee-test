package com.fitnesstracker.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fitnesstracker.data.database.entities.Exercise

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getExercisesForSession(sessionId: Long): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getExercisesForSessionSync(sessionId: Long): List<Exercise>

    @Query("SELECT DISTINCT name FROM exercises ORDER BY name ASC")
    suspend fun getAllExerciseNames(): List<String>

    @Query("SELECT DISTINCT name FROM exercises WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchExerciseNames(query: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<Exercise>)

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE sessionId = :sessionId")
    suspend fun deleteExercisesForSession(sessionId: Long)
}
