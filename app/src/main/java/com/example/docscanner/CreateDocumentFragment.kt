package com.example.docscanner

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.docscanner.databinding.FragmentCreateDocumentBinding
import java.lang.reflect.Method
import android.widget.Toast

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider


class CreateDocumentFragment : Fragment() {

    private lateinit var binding: FragmentCreateDocumentBinding
    private lateinit var createDocumentViewModel: CreateDocumentViewModel
    private lateinit var imageScannedAdapter: ImageScannedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateDocumentBinding.inflate(inflater, container, false)

        createDocumentViewModel = ViewModelProvider(this).get(CreateDocumentViewModel::class.java)

        imageScannedAdapter = ImageScannedAdapter(this, createDocumentViewModel.getImageScanned())
        binding.VP2ImageScanned.adapter = imageScannedAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            createDocumentFragment = this@CreateDocumentFragment
        }
    }

    fun onClickAddPage() {
        val popupAddPage = PopupMenu(requireContext(), binding.buttonAddPage)
        popupAddPage.menuInflater.inflate(R.menu.popup_add_page, popupAddPage.menu)

        popupAddPage.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_take_photo -> dispatchTakePictureIntent()
                R.id.action_select_photo -> imageChooseIntent()
            }
            true
        }

        // show icons on popup menu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupAddPage.setForceShowIcon(true)
        }else{
            try {
                val fields = popupAddPage.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popupAddPage]
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        popupAddPage.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Companion.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            createDocumentViewModel.addImageScanned(imageBitmap)
            imageScannedAdapter.notifyItemInserted(createDocumentViewModel.getImageScanned().size - 1)
            binding.VP2ImageScanned.setCurrentItem(createDocumentViewModel.getImageScanned().size - 1, true)
        }
        if (requestCode == Companion.REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            val imageBitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, data!!.data) as Bitmap
            createDocumentViewModel.addImageScanned(imageBitmap)
            imageScannedAdapter.notifyItemInserted(createDocumentViewModel.getImageScanned().size - 1)
            binding.VP2ImageScanned.setCurrentItem(createDocumentViewModel.getImageScanned().size - 1, true)
        }
    }

    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "You need to allow access camera permissions", Toast.LENGTH_SHORT).show()
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Companion.REQUEST_IMAGE_CAPTURE)
    }

    private fun imageChooseIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "You need to allow access storage permissions", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
            Companion.REQUEST_SELECT_PICTURE
        )
    }

    companion object {
        private const val REQUEST_SELECT_PICTURE = 2
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}