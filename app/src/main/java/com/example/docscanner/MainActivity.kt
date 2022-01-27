package com.example.docscanner

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.docscanner.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        OpenCVLoader.initDebug()
    }

    fun makeGray(bitmap: Bitmap) : Bitmap {
        // Create OpenCV mat object and copy content from bitmap
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Convert to grayscale
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

        // Make a mutable bitmap to copy grayscale image
        val grayBitmap = bitmap.copy(bitmap.config, true)
        Utils.matToBitmap(mat, grayBitmap)
        return grayBitmap
    }
}