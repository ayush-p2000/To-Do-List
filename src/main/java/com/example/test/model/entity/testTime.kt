package com.example.test.model.entity

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class testTime {
    fun main(){
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formatted = current.format(formatter).toInt()
    }
}