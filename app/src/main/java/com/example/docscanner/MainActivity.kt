package com.example.docscanner


import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.docscanner.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader
import java.util.ArrayList
import android.graphics.BitmapFactory
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import java.io.File
import android.graphics.Bitmap
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var pp = PictureProcessing()
    private val model: CreateDocumentViewModel by viewModels()

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

    private fun restoreLastSession() {

    }


    //    private fun change() {
//        val drawable = binding.imageView.drawable
//        var bitmap = drawable.toBitmap()
//        bitmap = pp.tf(bitmap)
//        binding.imageView.setImageBitmap(bitmap)
//    }
}