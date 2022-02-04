package com.example.docscanner

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.net.Uri
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.docscanner.databinding.DialogRenameFileLayoutBinding


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
            if (total > 0) {
                Toast.makeText(context, "Page $x/$total", Toast.LENGTH_SHORT).show()
            }
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

        imageScannedAdapter = ImageScannedAdapter(this, createDocumentViewModel.getImageScanned(), context)
        binding.VP2ImageScanned.adapter = imageScannedAdapter
        imageScannedAdapter.restoreLastSession()
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
        binding.VP2ImageScanned.unregisterOnPageChangeCallback(myPageChangeCallback)
        super.onDestroy()
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
        if (createDocumentViewModel.getImageScanned().size == 0) {
            return
        }
        Toast.makeText(context, "Del Page", Toast.LENGTH_SHORT).show()
        val currentItem = binding.VP2ImageScanned.currentItem
        imageScannedAdapter.removeImage(currentItem)
    }

    fun onClickRotate90() {
        if (createDocumentViewModel.getImageScanned().size == 0) {
            return
        }
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

    fun onClickRemoveAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Do you want to remove all?")

        builder.setPositiveButton("Yes") { dialog, which ->
            imageScannedAdapter.removeAll()
            val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
            val edit: SharedPreferences.Editor = sharedPreference.edit()
            edit.clear()
            edit.apply()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    fun onClickSave2PDF() {
        showDialogRename()
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
        val edit: SharedPreferences.Editor = sharedPreference.edit()
        edit.clear()
        edit.apply()
    }

    private fun showDialogRename() {
        val dialogBinding: DialogRenameFileLayoutBinding? =
            DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_rename_file_layout,
                null, false
            )
        val dialog = AlertDialog.Builder(requireContext(), 0).create()

        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setView(dialogBinding?.root)
            setCancelable(false)
        }.show()

        dialogBinding!!.editTextFileName.requestFocus()

        // Show keyboard
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        dialogBinding!!.btnCancel.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(dialogBinding!!.editTextFileName.windowToken, 0)
            dialog.dismiss()
        }

        dialogBinding!!.btnRename.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(dialogBinding!!.editTextFileName.windowToken, 0)
            PictureProcessing.toPdf(createDocumentViewModel.getImageScanned(), dialogBinding.editTextFileName.text.toString())
            dialog.dismiss()
        }

        dialogBinding!!.btnClearText.setOnClickListener {
            dialogBinding!!.editTextFileName.text.clear()
        }
    }

    companion object {
        private const val REQUEST_SELECT_PICTURE = 2
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val imageScannedName2Save = "Image Scanned Temp "
    }

    override fun onResume() {
        imageScannedAdapter.reload()
        super.onResume()
    }
}