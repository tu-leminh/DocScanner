package com.example.docscanner.reorder

interface ItemTouchListener {
    fun onItemMove(oldPosition: Int, newPosition: Int) : Boolean

    fun onItemDismiss(position: Int)
}