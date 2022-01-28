package com.example.docscanner

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel

class CreateDocumentViewModel : ViewModel() {
    private var imageScanned = ArrayList<Bitmap>()

    fun getImageScanned() = imageScanned
}