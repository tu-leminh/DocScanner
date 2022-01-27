package com.example.docscanner


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.example.docscanner.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var pp = PictureProcessing()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageView.setOnClickListener { change() }
        OpenCVLoader.initDebug()
    }

    private fun change() {
        val drawable = binding.imageView.drawable
        var bitmap = drawable.toBitmap()
        bitmap = pp.tf(bitmap)
        binding.imageView.setImageBitmap(bitmap)
    }

}