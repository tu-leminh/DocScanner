package com.example.docscanner


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.NavHostFragment
import com.example.docscanner.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var pp = PictureProcessing()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        //binding.imageView.setOnClickListener { change() }
        OpenCVLoader.initDebug()
        ////

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

//    private fun change() {
//        val drawable = binding.imageView.drawable
//        var bitmap = drawable.toBitmap()
//        bitmap = pp.tf(bitmap)
//        binding.imageView.setImageBitmap(bitmap)
//    }

}