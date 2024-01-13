package com.example.test.data

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseInitializer {
    private const val DB_NAME = "TestDataBase"

    fun initialize(context: Context): TestDatabase {
        Log.d("DatabaseInitializer", "Initializing database...")
        val database = Room.databaseBuilder(
            context.applicationContext,
            TestDatabase::class.java, DB_NAME
        ).build()
        Log.d("DatabaseInitializer", "Database initialized successfully")
        return database
    }
}


