package com.example.docscanner

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImageScannedAdapter(fa: Fragment, imageScanned: ArrayList<Bitmap>) : FragmentStateAdapter(fa) {
    private var images: ArrayList<Bitmap> = imageScanned

    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment = SingleImageFragment(images[position])
}
