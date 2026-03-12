package com.fitnesstracker.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
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
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val date: Long,                     // epoch ms
    val bodyPart: String,               // e.g. "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Full Body"
    val durationMinutes: Int = 0,
    val notes: String = ""
)
