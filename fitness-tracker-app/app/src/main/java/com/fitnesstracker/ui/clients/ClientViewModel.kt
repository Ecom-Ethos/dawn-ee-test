package com.fitnesstracker.ui.clients

import androidx.lifecycle.*
import com.fitnesstracker.data.database.entities.Client
import com.fitnesstracker.data.repository.FitnessRepository
import kotlinx.coroutines.launch

class ClientViewModel(private val repository: FitnessRepository) : ViewModel() {

    private val _searchQuery = MutableLiveData<String>("")
    val clients: LiveData<List<Client>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) repository.getAllClients()
        else repository.searchClients(query)
    }

    val clientCount: LiveData<Int> = repository.getClientCount()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertClient(client: Client) = viewModelScope.launch {
        repository.insertClient(client)
    }

    fun updateClient(client: Client) = viewModelScope.launch {
        repository.updateClient(client)
    }

    fun deleteClient(client: Client) = viewModelScope.launch {
        repository.deleteClient(client)
    }

    fun getClientById(clientId: Long): LiveData<Client> = repository.getClientById(clientId)
}

class ClientViewModelFactory(private val repository: FitnessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
