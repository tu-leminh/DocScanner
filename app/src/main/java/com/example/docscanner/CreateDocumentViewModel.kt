package com.example.docscanner

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlin.collections.ArrayList

class CreateDocumentViewModel : ViewModel() {
    private var imageScanned = ArrayList<Bitmap>()

    fun getImageScanned() = imageScanned

    fun reorder(newOrder: ArrayList<Int>) {
        for(i in newOrder.indices) {
            if (i != newOrder[i])  {
                var temp: Bitmap = imageScanned[i]
                imageScanned[i] = imageScanned[newOrder[i]]
                imageScanned[newOrder[i]] = temp

                for (j in newOrder.indices)
                {
                    if (newOrder[j] == i)
                    {
                        newOrder[j] = newOrder[i]
                        newOrder[i] = i
                    }
                }
            }
        }
    }
}