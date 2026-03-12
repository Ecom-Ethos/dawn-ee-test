package com.fitnesstracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String,         // "Male" / "Female" / "Other"
    val phone: String = "",
    val email: String = "",
    val goal: String = "",      // e.g. Weight Loss, Muscle Gain, Endurance
    val createdAt: Long = System.currentTimeMillis()
)
