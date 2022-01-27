package com.example.docscanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.example.docscanner.databinding.FragmentCreateDocumentBinding

class CreateDocumentFragment : Fragment() {
    private lateinit var binding: FragmentCreateDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun onClickAddPage() {
        val popupAddPage = PopupMenu(requireContext(), binding.buttonAddPage)
        popupAddPage.menuInflater.inflate(R.menu.popup_add_page, popupAddPage.menu)
        popupAddPage.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_take_photo -> {}
                R.id.action_select_photo -> {}
            }
            true
        }
        popupAddPage.show()
    }
}