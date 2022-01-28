package com.example.docscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel

class CreateDocumentViewModel : ViewModel() {
    private var imageScanned = ArrayList<Bitmap>()

    init {

    }
    fun addImageScanned(image: Bitmap) {
        imageScanned.add(image)
    }

    fun deleteImageScanned(position: Int) {
        imageScanned.removeAt(position)
    }

    fun getImageScanned() = imageScanned
}