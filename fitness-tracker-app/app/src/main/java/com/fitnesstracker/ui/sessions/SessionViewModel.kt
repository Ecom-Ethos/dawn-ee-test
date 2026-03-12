package com.fitnesstracker.ui.sessions

import androidx.lifecycle.*
import com.fitnesstracker.data.database.entities.Exercise
import com.fitnesstracker.data.database.entities.Session
import com.fitnesstracker.data.repository.FitnessRepository
import kotlinx.coroutines.launch

class SessionViewModel(private val repository: FitnessRepository) : ViewModel() {

    private val _clientId = MutableLiveData<Long>()

    val sessions: LiveData<List<Session>> = _clientId.switchMap { clientId ->
        repository.getSessionsForClient(clientId)
    }

    private val _sessionId = MutableLiveData<Long>()

    val exercises: LiveData<List<Exercise>> = _sessionId.switchMap { sessionId ->
        repository.getExercisesForSession(sessionId)
    }

    fun setClientId(clientId: Long) {
        _clientId.value = clientId
    }

    fun setSessionId(sessionId: Long) {
        _sessionId.value = sessionId
    }

    fun insertSession(session: Session, exercises: List<Exercise>) = viewModelScope.launch {
        val sessionId = repository.insertSession(session)
        val exercisesWithSessionId = exercises.map { it.copy(sessionId = sessionId) }
        repository.insertExercises(exercisesWithSessionId)
    }

    fun updateSession(session: Session, exercises: List<Exercise>) = viewModelScope.launch {
        repository.updateSession(session)
        repository.deleteExercisesForSession(session.id)
        val exercisesWithSessionId = exercises.map { it.copy(sessionId = session.id) }
        repository.insertExercises(exercisesWithSessionId)
    }

    fun deleteSession(session: Session) = viewModelScope.launch {
        repository.deleteSession(session)
    }

    suspend fun getExerciseNamesForAutocomplete(query: String): List<String> =
        repository.searchExerciseNames(query)
}

class SessionViewModelFactory(private val repository: FitnessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
