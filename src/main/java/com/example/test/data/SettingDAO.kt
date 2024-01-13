package com.example.test.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.test.model.entity.SettingEntity

@Dao
interface SettingDAO {
    @Insert
    suspend fun insertSettingEntity(settingEntity: SettingEntity)

    @Query("SELECT * FROM " + SettingEntity.TABLE_NAME)
    fun getAllSettingEntities(): List<SettingEntity>

    @Query("SELECT COUNT(*) FROM"+ SettingEntity.TABLE_NAME)
    fun getMyEntitiesCount(): Int

    @Update
    suspend fun updateSettingEntity(settingEntity: SettingEntity)
}