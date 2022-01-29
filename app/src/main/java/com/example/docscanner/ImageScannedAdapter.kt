package com.example.docscanner

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImageScannedAdapter(fa: Fragment, imageScanned: ArrayList<Bitmap>) : FragmentStateAdapter(fa) {

    private var images: ArrayList<Bitmap> = imageScanned
    private val pageIds= images.map { it.hashCode().toLong() }
    private val pp = PictureProcessing()

    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment = SingleImageFragment(images[position])

    fun addImage(image: Bitmap) {
        //images.add(pp.tf(image))
        images.add(image)
        notifyDataSetChanged()
    }

    fun removeImage(position: Int) {
        images.removeAt(position)
        notifyDataSetChanged()
    }

    fun rotate90(position: Int) {
        val matrix = Matrix()
        matrix.postRotate(90F)
        val imgOrigin = images[position]
        images[position] = Bitmap.createBitmap(imgOrigin, 0, 0, imgOrigin.width, imgOrigin.height, matrix, true)
        notifyDataSetChanged()
    }

    fun reload() {
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return images[position].hashCode().toLong() // make sure notifyDataSetChanged() works
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }
}
