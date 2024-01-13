package com.example.test.data

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseInitializer2 {
    private const val DB_NAME = "SD"

    fun initialize2(context: Context): SettingDatabase {
        Log.d("DatabaseInitializer2", "Initializing database2...")
        val database = Room.databaseBuilder(
            context.applicationContext,
            SettingDatabase::class.java, DB_NAME
        ).build()
        Log.d("DatabaseInitializer2", "Database2 initialized successfully")
        return database
    }
}