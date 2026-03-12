package com.fitnesstracker

import android.app.Application
import com.fitnesstracker.data.database.AppDatabase
import com.fitnesstracker.data.repository.FitnessRepository

class FitnessTrackerApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        FitnessRepository(
            database.clientDao(),
            database.measurementDao(),
            database.sessionDao(),
            database.exerciseDao()
        )
    }
}
