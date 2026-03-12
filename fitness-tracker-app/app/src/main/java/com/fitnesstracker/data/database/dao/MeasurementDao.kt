package com.fitnesstracker.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fitnesstracker.data.database.entities.Measurement

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurements WHERE clientId = :clientId ORDER BY date DESC")
    fun getMeasurementsForClient(clientId: Long): LiveData<List<Measurement>>

    @Query("SELECT * FROM measurements WHERE id = :measurementId")
    suspend fun getMeasurementById(measurementId: Long): Measurement?

    @Query("SELECT * FROM measurements WHERE clientId = :clientId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMeasurement(clientId: Long): Measurement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: Measurement): Long

    @Update
    suspend fun updateMeasurement(measurement: Measurement)

    @Delete
    suspend fun deleteMeasurement(measurement: Measurement)
}
