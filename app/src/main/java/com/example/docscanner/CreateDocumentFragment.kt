package com.example.docscanner

import android.Manifest
import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.ContentValues
import android.R.attr.thumbnail
import android.net.Uri


class CreateDocumentFragment : Fragment() {

    private lateinit var binding: FragmentCreateDocumentBinding
    private val createDocumentViewModel: CreateDocumentViewModel by activityViewModels()
    private lateinit var imageScannedAdapter: ImageScannedAdapter

    private lateinit var values: ContentValues
    private lateinit var imageUri: Uri

    private var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val x = position + 1
            val total = createDocumentViewModel.getImageScanned().size
            Toast.makeText(context, "Page $x/$total", Toast.LENGTH_SHORT).show()
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateDocumentBinding.inflate(inflater, container, false)

        imageScannedAdapter = ImageScannedAdapter(this, createDocumentViewModel.getImageScanned())
        binding.VP2ImageScanned.adapter = imageScannedAdapter

        setUpIntentCamera()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            createDocumentFragment = this@CreateDocumentFragment
        }

        setOnPageChange()
    }

    private fun setOnPageChange() {
        binding.VP2ImageScanned.registerOnPageChangeCallback(myPageChangeCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.VP2ImageScanned.unregisterOnPageChangeCallback(myPageChangeCallback)
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

    fun onClickDelPage() {
        Toast.makeText(context, "Del Page", Toast.LENGTH_LONG).show()
        val currentItem = binding.VP2ImageScanned.currentItem
        imageScannedAdapter.removeImage(currentItem)
    }

    fun onClickRotate90() {
        val currentPos = binding.VP2ImageScanned.currentItem
        imageScannedAdapter.rotate90(currentPos)
    }

    fun onClickReorder() {
        findNavController().navigate(R.id.action_createDocumentFragment_to_reorderImageScannedFragment)
        setFragmentResultListener("ChangeOrder") { _, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported
            val result = bundle.getString("isOkay")
            // Do something with the result
            if (result == "ok")
            {
                imageScannedAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //val imageBitmap = data!!.extras!!.get("data") as Bitmap
            try {
                val thumbnail = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                imageScannedAdapter.addImage(thumbnail)
                binding.VP2ImageScanned.setCurrentItem(createDocumentViewModel.getImageScanned().size - 1, true)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (requestCode == Companion.REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            val imageBitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, data!!.data) as Bitmap
            imageScannedAdapter.addImage(imageBitmap)
            binding.VP2ImageScanned.setCurrentItem(createDocumentViewModel.getImageScanned().size - 1, true)
        }
    }

//    private fun getRealPathFromURI(imageUri: Uri) {
//        val proj = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
//        val column_index: Int = cursor
//            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        cursor.moveToFirst()
//        return cursor.getString(column_index)
//    }

    private fun setUpIntentCamera() {
        values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )!!
    }

    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "You need to allow access camera permissions", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun imageChooseIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "You need to allow access storage permissions", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_SELECT_PICTURE)
    }

    companion object {
        private const val REQUEST_SELECT_PICTURE = 2
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onStart() {
        imageScannedAdapter.reload()
        super.onStart()
    }
}