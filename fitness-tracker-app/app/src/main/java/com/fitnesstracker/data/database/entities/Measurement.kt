package com.fitnesstracker.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "measurements",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val date: Long,                     // epoch ms
    val weight: Float = 0f,             // kg
    val height: Float = 0f,             // cm
    val chest: Float = 0f,              // cm
    val waist: Float = 0f,              // cm
    val hips: Float = 0f,               // cm
    val leftBicep: Float = 0f,          // cm
    val rightBicep: Float = 0f,         // cm
    val leftThigh: Float = 0f,          // cm
    val rightThigh: Float = 0f,         // cm
    val leftCalf: Float = 0f,           // cm
    val rightCalf: Float = 0f,          // cm
    val bodyFatPercent: Float = 0f,
    val notes: String = ""
)
