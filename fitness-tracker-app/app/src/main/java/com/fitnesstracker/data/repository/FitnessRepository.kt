package com.fitnesstracker.data.repository

import androidx.lifecycle.LiveData
import com.fitnesstracker.data.database.dao.*
import com.fitnesstracker.data.database.entities.*

class FitnessRepository(
    private val clientDao: ClientDao,
    private val measurementDao: MeasurementDao,
    private val sessionDao: SessionDao,
    private val exerciseDao: ExerciseDao
) {
    // ─── Clients ──────────────────────────────────────────────────────────────
    fun getAllClients(): LiveData<List<Client>> = clientDao.getAllClients()
    fun getClientById(id: Long): LiveData<Client> = clientDao.getClientById(id)
    fun searchClients(query: String): LiveData<List<Client>> = clientDao.searchClients(query)
    fun getClientCount(): LiveData<Int> = clientDao.getClientCount()

    suspend fun insertClient(client: Client): Long = clientDao.insertClient(client)
    suspend fun updateClient(client: Client) = clientDao.updateClient(client)
    suspend fun deleteClient(client: Client) = clientDao.deleteClient(client)
    suspend fun getClientByIdSync(id: Long): Client? = clientDao.getClientByIdSync(id)

    // ─── Measurements ─────────────────────────────────────────────────────────
    fun getMeasurementsForClient(clientId: Long): LiveData<List<Measurement>> =
        measurementDao.getMeasurementsForClient(clientId)

    suspend fun getLatestMeasurement(clientId: Long): Measurement? =
        measurementDao.getLatestMeasurement(clientId)

    suspend fun insertMeasurement(measurement: Measurement): Long =
        measurementDao.insertMeasurement(measurement)

    suspend fun updateMeasurement(measurement: Measurement) =
        measurementDao.updateMeasurement(measurement)

    suspend fun deleteMeasurement(measurement: Measurement) =
        measurementDao.deleteMeasurement(measurement)

    // ─── Sessions ─────────────────────────────────────────────────────────────
    fun getSessionsForClient(clientId: Long): LiveData<List<Session>> =
        sessionDao.getSessionsForClient(clientId)

    fun getSessionCountForClient(clientId: Long): LiveData<Int> =
        sessionDao.getSessionCountForClient(clientId)

    suspend fun insertSession(session: Session): Long = sessionDao.insertSession(session)
    suspend fun updateSession(session: Session) = sessionDao.updateSession(session)
    suspend fun deleteSession(session: Session) = sessionDao.deleteSession(session)
    suspend fun getSessionById(id: Long): Session? = sessionDao.getSessionById(id)

    // ─── Exercises ────────────────────────────────────────────────────────────
    fun getExercisesForSession(sessionId: Long): LiveData<List<Exercise>> =
        exerciseDao.getExercisesForSession(sessionId)

    suspend fun getExercisesForSessionSync(sessionId: Long): List<Exercise> =
        exerciseDao.getExercisesForSessionSync(sessionId)

    suspend fun getAllExerciseNames(): List<String> = exerciseDao.getAllExerciseNames()

    suspend fun searchExerciseNames(query: String): List<String> =
        exerciseDao.searchExerciseNames(query)

    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)
    suspend fun insertExercises(exercises: List<Exercise>) = exerciseDao.insertExercises(exercises)
    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)
    suspend fun deleteExercisesForSession(sessionId: Long) =
        exerciseDao.deleteExercisesForSession(sessionId)
}
