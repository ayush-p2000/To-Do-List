package com.example.test.model.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(KeyEntity.TABLE_NAME)
@TypeConverters(Converters::class)
class SettingEntity (
    @PrimaryKey(autoGenerate = true)
    val sid: Int,
    val themeColor: Int,
    val priorityColor: Int,
    val uid: String,
    val uri: List<Uri>
){
    companion object {
        const val TABLE_NAME = "keys"
    }
}