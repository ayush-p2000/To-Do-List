/*The code in these page is written by Siyuan Peng*/

package com.example.test.model.entity

import android.net.Uri
import com.example.test.ui.screens.ToDoItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TodoItem(
    var id: Int,
    var title: String,
    var time: Int,
    var details: String,
    var check: MutableList<ToDoItem>,
    var priority: Boolean,
    var pictures: MutableList<Uri>,
    var locationTag: String,
    var checklist: String,
    var hour:Int,
    var minute:Int
    //val photo: String
){
    fun toKeyEntity(): KeyEntity {
        return KeyEntity(
            id = id,
            title = title,
            description = details,
            isLocationEnabled = false, // Set this based on your requirements
            isPriorityEnabled = priority,
            dueDate = DueDate(
                day = time % 100,
                month = (time % 10000) / 100,
                year = time / 10000
            ),
            todoList = check.toList(),
            selectedFiles = pictures.toList(),
            cameraImageUri = null, // Set this based on your requirements
            cameraImageBitmap = null, // Set this based on your requirements
            selectedDay = time % 100, // Set this based on your requirements
            selectedMonth = (time % 10000) / 100, // Set this based on your requirements
            selectedYear =  time / 10000, // Set this based on your requirements
            locationTag = locationTag,
            checklist = checklist,
            hour = hour,
            minute = minute
        )
    }

    companion object {
        // Factory method to create TodoItem from KeyEntity
        fun fromKeyEntity(keyEntity: KeyEntity): TodoItem {
            return TodoItem(
                id = keyEntity.id,
                title = keyEntity.title,
                time = keyEntity.selectedYear * 10000 + keyEntity.selectedMonth * 100 + keyEntity.selectedDay,
                details = keyEntity.description,
                check = keyEntity.todoList.toMutableList(),
                priority = keyEntity.isPriorityEnabled,
                pictures = keyEntity.selectedFiles.toMutableList(),
                locationTag = keyEntity.locationTag,
                checklist = keyEntity.checklist,
                hour = keyEntity.hour,
                minute = keyEntity.minute
            )
        }
    }

    fun getTime():String{
        if(hour<10&&minute<10){
            return "0$hour : 0$minute"
        }else if(minute<10){
            return "$hour : 0$minute"
        }else if(hour<10){
            return "0$hour : $minute"
        }
        else{
            return "$hour : $minute"
        }
    }


    fun doesMatchSearchText(searchText:String):Boolean{
        val matchCombinations = listOf(
            "$title",
            "$details",
        )

        return matchCombinations.any{
            it.contains(searchText,ignoreCase = true)
        }
    }

    fun hasBeenHistory():Boolean{
        val current = LocalDateTime.now()
        val chour = current.hour
        val cminute = current.minute
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formattedTime = current.format(formatter).toInt()

        if(time<formattedTime){
            return true
        }
        else if(time==formattedTime&& hour< chour){
            return true
        }
        else if(time==formattedTime&& hour == chour && minute < cminute){
            return true
        }

        return false
    }

    fun hasCompleted():Boolean{
        return checklist.all { it == '1' }
    }
}
