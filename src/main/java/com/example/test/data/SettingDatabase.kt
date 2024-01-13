package com.example.test.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.test.model.entity.SettingEntity

@Database(entities = [SettingEntity::class], version = 3, exportSchema = false)
abstract class SettingDatabase : RoomDatabase() {
    abstract fun settingDAO(): SettingDAO

    companion object {
        private const val DB_NAME = "SettingDao"

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                SettingDatabase::class.java, // 修改这里
                DB_NAME
            ).fallbackToDestructiveMigration().build()

        @Volatile
        private var thisDB: SettingDatabase? = null

        fun getDB(context: Context): SettingDatabase =
            thisDB ?: synchronized(this) {
                thisDB ?: buildDatabase(context).also { thisDB = it }
            }

    }
}
