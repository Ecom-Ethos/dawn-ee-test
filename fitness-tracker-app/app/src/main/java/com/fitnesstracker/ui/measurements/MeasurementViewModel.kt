package com.fitnesstracker.ui.measurements

import androidx.lifecycle.*
import com.fitnesstracker.data.database.entities.Measurement
import com.fitnesstracker.data.repository.FitnessRepository
import kotlinx.coroutines.launch

class MeasurementViewModel(private val repository: FitnessRepository) : ViewModel() {

    private val _clientId = MutableLiveData<Long>()

    val measurements: LiveData<List<Measurement>> = _clientId.switchMap { clientId ->
        repository.getMeasurementsForClient(clientId)
    }

    fun setClientId(clientId: Long) {
        _clientId.value = clientId
    }

    fun insertMeasurement(measurement: Measurement) = viewModelScope.launch {
        repository.insertMeasurement(measurement)
    }

    fun updateMeasurement(measurement: Measurement) = viewModelScope.launch {
        repository.updateMeasurement(measurement)
    }

    fun deleteMeasurement(measurement: Measurement) = viewModelScope.launch {
        repository.deleteMeasurement(measurement)
    }
}

class MeasurementViewModelFactory(private val repository: FitnessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeasurementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeasurementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
