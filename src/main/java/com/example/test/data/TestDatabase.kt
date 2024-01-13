package com.example.test.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.test.model.entity.Converters
import com.example.test.model.entity.KeyEntity

// Database for the application
@Database(entities = [KeyEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TestDatabase : RoomDatabase() {
    abstract fun keyDAO(): KeyDAO // Updated function name

    companion object {
        private const val DB_NAME = "TestDataBase"

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                TestDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration().build()

        @Volatile private var thisDB: TestDatabase? = null

        fun getDB(context: Context): TestDatabase =
            thisDB ?: synchronized(this) {
                thisDB ?: buildDatabase(context).also { thisDB = it }
            }
    }
}