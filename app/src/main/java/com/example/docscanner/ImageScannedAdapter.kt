package com.example.docscanner

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

class ImageScannedAdapter (imageScanned: ArrayList<Bitmap>)
    : RecyclerView.Adapter<ImageScannedAdapter.ImageViewHolder>() {
    private lateinit var images: ArrayList<Bitmap>

    private var numberOfImage: Int = 0

    init {
        images = imageScanned
        numberOfImage = imageScanned.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_image_scanned, parent, false))

    override fun onBindViewHolder(holder: ImageScannedAdapter.ImageViewHolder, position: Int) = holder.itemView.run {
        if (position >= 0) {
            findViewById<ImageView>(R.id.imageScanned).setImageBitmap(images[position])
        }
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}