package com.example.docscanner.reorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.docscanner.CreateDocumentViewModel
import com.example.docscanner.R
import com.example.docscanner.databinding.FragmentReorderImageScannedBinding

class ReorderImageScannedFragment : Fragment() {
    private lateinit var binding: FragmentReorderImageScannedBinding
    private val createDocumentViewModel: CreateDocumentViewModel by activityViewModels()

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var _items: ArrayList<ItemImageScanned>
    private lateinit var adapter: RISRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReorderImageScannedBinding.inflate(inflater, container, false)

        updateGridView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            reorderImageScannedFragment = this@ReorderImageScannedFragment
        }
    }

    private fun updateGridView() {
        binding.RVImageScanned.setHasFixedSize(true)
        _items = discoverItems()
        adapter = RISRecyclerViewAdapter(requireContext(), _items, listener())
        binding.RVImageScanned.adapter = adapter
        val callback = ItemRISTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.RVImageScanned)
    }

    inner class listener() : OnStartDragListener {
        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }
    }

    private fun discoverItems(): ArrayList<ItemImageScanned> {
        var arrItems = ArrayList<ItemImageScanned>()
        for (i in createDocumentViewModel.getImageScanned().indices) {
            var _item = ItemImageScanned(createDocumentViewModel.getImageScanned()[i], (i + 1).toString())
            arrItems.add(_item)
        }
        return arrItems
    }

    fun onClickCancel() {
        findNavController().navigate(R.id.action_reorderImageScannedFragment_to_createDocumentFragment)
    }

    fun onClickAccept() {
        var newOrder = ArrayList<Int>()
        for (item in _items) {
            newOrder.add(item.title.toInt() - 1)
        }
        createDocumentViewModel.reorder(newOrder)
        findNavController().navigate(R.id.action_reorderImageScannedFragment_to_createDocumentFragment)
    }
}