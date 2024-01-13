package com.example.test.model.entity
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.TypeConverter
import com.example.test.model.entity.DueDate
import com.example.test.ui.screens.ToDoItem
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type

class Converters {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): DueDate? {
        return value?.let {
            val date = dateFormat.parse(value)
            val calendar = Calendar.getInstance()
            calendar.time = date
            DueDate(
                day = calendar.get(Calendar.DAY_OF_MONTH),
                month = calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is zero-based
                year = calendar.get(Calendar.YEAR)
            )
        }
    }

    @TypeConverter
    fun toString(date: DueDate?): String? {
        return date?.let {
            val calendar = Calendar.getInstance()
            calendar.set(date.year, date.month - 1, date.day) // Calendar.MONTH is zero-based
            dateFormat.format(calendar.time)
        }
    }

    @TypeConverter
    fun fromTodoList(value: String?): List<ToDoItem>? {
        val type: Type = object : TypeToken<List<ToDoItem>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toTodoList(todoList: List<ToDoItem>?): String? {
        return gson.toJson(todoList)
    }

    @TypeConverter
    fun fromUriList(value: String?): List<Uri>? {
        val type: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(value, type)?.map { Uri.parse(it) }
    }

    @TypeConverter
    fun toUriList(uris: List<Uri>?): String? {
        val type: Type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(uris?.map { it.toString() }, type)
    }

}
