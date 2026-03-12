package com.fitnesstracker.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fitnesstracker.data.database.entities.Client

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAllClients(): LiveData<List<Client>>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientById(clientId: Long): LiveData<Client>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientByIdSync(clientId: Long): Client?

    @Query("SELECT * FROM clients WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchClients(query: String): LiveData<List<Client>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long

    @Update
    suspend fun updateClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)

    @Query("SELECT COUNT(*) FROM clients")
    fun getClientCount(): LiveData<Int>
}
