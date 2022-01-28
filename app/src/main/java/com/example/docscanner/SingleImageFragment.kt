package com.example.docscanner

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.docscanner.databinding.FragmentSingleImageBinding

class SingleImageFragment(img: Bitmap) : Fragment() {
    private lateinit var binding: FragmentSingleImageBinding
    private var img = img
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingleImageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageScanned.setImageBitmap(img)
    }
}