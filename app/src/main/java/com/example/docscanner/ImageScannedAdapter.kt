package com.example.docscanner

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.os.Environment




class ImageScannedAdapter(fa: Fragment, imageScanned: ArrayList<Bitmap>, context: Context?) : FragmentStateAdapter(fa) {
    private val context = context
    private var images: ArrayList<Bitmap> = imageScanned
    private val pageIds= images.map { it.hashCode().toLong() }
    private val pp = PictureProcessing()
    private val dir = "/data/local/tmp/DocScanner"

    fun restoreLastSession() {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)

        val count = sharedPreference.getInt("lastCountImg", 0)

        for (i in 0 until count)
        {
            val path = sharedPreference.getString(i.toString(), "")
            val imgFile = File(path)

            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                images.add(myBitmap)
            }
        }

        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment = SingleImageFragment(images[position])

    fun addImage(image: Bitmap) {
        images.add(pp.tf(image))
        //pp.toPdf(images,"test")
        //images.add(image)
        saveToStorage()
        notifyDataSetChanged()
    }

    fun removeImage(position: Int) {
        images.removeAt(position)
        saveToStorage()
        notifyDataSetChanged()
    }

    fun rotate90(position: Int) {
        val matrix = Matrix()
        matrix.postRotate(90F)
        val imgOrigin = images[position]
        images[position] = Bitmap.createBitmap(imgOrigin, 0, 0, imgOrigin.width, imgOrigin.height, matrix, true)
        notifyDataSetChanged()
    }

    fun removeAll() {
        images.clear()
        notifyDataSetChanged()
    }

    fun reload() {
        notifyDataSetChanged()
    }

    private fun saveToStorage()
    {
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val edit: SharedPreferences.Editor = sharedPreference.edit()

        val count = images.size
        edit.putInt("lastCountImg", count)
        val i = count - 1

        val dir_path = File(dir)
        if(!dir_path.exists())
            dir_path.mkdirs();

        val file: File =
            File(Environment.getExternalStorageDirectory().toString(), "$i.png")
        val bitmapFile = images[i]

        try {
            val use = FileOutputStream(file).use { out ->
                bitmapFile.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            edit.putString(i.toString(), file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        edit.commit()
    }

    override fun getItemId(position: Int): Long {
        return images[position].hashCode().toLong() // make sure notifyDataSetChanged() works
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }
}
