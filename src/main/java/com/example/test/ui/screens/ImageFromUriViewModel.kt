package com.example.test.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ImageFromUriViewModel : ViewModel() {
    var imageBitmap by mutableStateOf<android.graphics.Bitmap?>(null)

    fun updateImage(bitmap: android.graphics.Bitmap) {
        imageBitmap = bitmap
    }
}