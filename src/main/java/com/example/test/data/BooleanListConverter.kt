/*The code in these page is written by Siyuan Peng*/

package com.example.test.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BooleanListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromBooleanList(list: List<Boolean>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toBooleanList(data: String): List<Boolean> {
        val listType = object : TypeToken<List<Boolean>>() {}.type
        return gson.fromJson(data, listType)
    }
}