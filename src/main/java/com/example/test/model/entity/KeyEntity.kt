package com.example.test.model.entity
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.test.ui.screens.ToDoItem


// Database table and it's attributes
@Entity(KeyEntity.TABLE_NAME)
@TypeConverters(Converters::class)
data class KeyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isLocationEnabled: Boolean,
    val isPriorityEnabled: Boolean,
    val dueDate: DueDate, // You need to define a DueDate class or use separate fields for day, month, year.
    val todoList: List<ToDoItem>, // Subtasks
    val selectedFiles: List<Uri>,
    val cameraImageUri: String?,
    val cameraImageBitmap: String?,
    val selectedDay: Int,
    val selectedMonth: Int,
    val selectedYear: Int,
    val locationTag: String,
    val checklist: String,
    val hour: Int,
    val minute: Int
){
    companion object {
        const val TABLE_NAME = "keys"
    }
}
data class DueDate(val day: Int, val month: Int, val year: Int)

