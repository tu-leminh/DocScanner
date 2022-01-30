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
        requestPermission()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        restoreLastSession()

    }

    private fun restoreLastSession() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)

        val count = sharedPreference.getInt("lastCountImg", 0)

        for (i in 0..count)
        {
            val path = sharedPreference.getString(i.toString(), "")
            val imgFile = File(path)

            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                model.getImageScanned().add(myBitmap)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val edit: SharedPreferences.Editor = sharedPreference.edit()

        val count = model.getImageScanned().size
        edit.putInt("lastCountImg", count)

        for (i in 0..count)
        {
            val path = externalCacheDir?.absolutePath.toString() + count.toString() + ".PNG"
            val bitmapFile = model.getImageScanned()[i]
            val fileAtPath = File(path)

            try {
                val use = FileOutputStream(fileAtPath).use { out ->
                    bitmapFile.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        edit.commit()
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