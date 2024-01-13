package com.example.test.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.test.model.entity.KeyEntity


// DAO implementing the operations of the database
@Dao
interface KeyDAO {
    @Insert
    suspend fun insertKeyEntity(keyEntity: KeyEntity)

    @Query("SELECT * FROM " + KeyEntity.TABLE_NAME)
    fun getAllKeyEntities(): List<KeyEntity>


    @Query("SELECT * FROM " + KeyEntity.TABLE_NAME + " WHERE title = :title ")
    fun getKeyEntityByTitle(title: String): KeyEntity?

    @Query("SELECT * FROM " + KeyEntity.TABLE_NAME + " WHERE id = :id ")
    fun getKeyEntityById(id: Int): KeyEntity?

    @Update
    suspend fun updateKeyEntity(keyEntity: KeyEntity)

    @Delete
    suspend fun deleteKeyEntity(keyEntity: KeyEntity)

    @Query("SELECT * FROM " + KeyEntity.TABLE_NAME + " ORDER BY title ASC")
    fun getSortedByTitle(): List<KeyEntity>

    @Query("SELECT * FROM " + KeyEntity.TABLE_NAME + " ORDER BY selectedYear ASC, selectedMonth ASC, selectedDay ASC")
    fun getSortedByDate(): List<KeyEntity>

    @Query("SELECT * FROM " + KeyEntity.TABLE_NAME + " ORDER BY isPriorityEnabled DESC, selectedYear ASC, selectedMonth ASC, selectedDay ASC")
    fun getSortedByPriority(): List<KeyEntity>
}