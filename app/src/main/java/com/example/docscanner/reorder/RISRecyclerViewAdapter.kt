package com.example.docscanner.reorder

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.docscanner.R
import java.util.*
import kotlin.collections.ArrayList

class RISRecyclerViewAdapter (
    context: Context,
    dataArrayList: ArrayList<ItemImageScanned>,
    listener: OnStartDragListener
    ): RecyclerView.Adapter<RISRecyclerViewAdapter.RecyclerViewHolder>(), ItemTouchListener {

    private var _items = dataArrayList
    private val _context = context
    private val listener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(LayoutInflater
        .from(_context).inflate(R.layout.layout_item_reorder_image_scanned, parent, false))

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.imageScanned.setImageBitmap(_items[position].imageScanned)
        holder.title.text = _items[position].title

        holder.item.setOnLongClickListener({
            listener.onStartDrag(holder)
            false
        })
    }

    override fun getItemCount(): Int = _items.size

    inner class RecyclerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageScanned = itemView.findViewById<ImageView>(R.id.IVItemImageScanned)
        val title = itemView.findViewById<TextView>(R.id.titleItemImageScanned)
        val item = itemView.findViewById<LinearLayout>(R.id.itemRIS)
    }

    override fun onItemMove(oldPosition: Int, newPosition: Int): Boolean {
        if (oldPosition < newPosition) {
            for (i in oldPosition until newPosition) {
                Collections.swap(_items, i, i + 1)
            }
        } else {
            for (i in oldPosition downTo newPosition + 1) {
                Collections.swap(_items, i, i - 1)
            }
        }
        notifyItemMoved(oldPosition, newPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        _items.removeAt(position)
        notifyItemRemoved(position)
    }
}