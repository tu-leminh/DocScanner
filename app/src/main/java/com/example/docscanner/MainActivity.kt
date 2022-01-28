package com.example.docscanner


import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.docscanner.databinding.ActivityMainBinding
import org.opencv.android.OpenCVLoader
import java.util.ArrayList


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
        requestPermission()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

//    private fun change() {
//        val drawable = binding.imageView.drawable
//        var bitmap = drawable.toBitmap()
//        bitmap = pp.tf(bitmap)
//        binding.imageView.setImageBitmap(bitmap)
//    }
    private fun requestPermission() {
        val permissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        val arrayPermissions = arrayOfNulls<String>(permissions.size)
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(arrayPermissions), 100)
        }
    }
}