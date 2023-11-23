package com.varsitycollege.mediaspace.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.varsitycollege.mediaspace.R

class SizeAdapter(private val context: Context, private val product: Product) : BaseAdapter() {

    private val sizeList: List<Size> = product.sizeList.orEmpty()
    private val selectedItems = HashSet<Int>() // Keep track of selected items

    override fun getCount(): Int {
        return sizeList.size
    }

    override fun getItem(position: Int): Any {
        return sizeList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val sizes = sizeList[position]

        val view: TextView = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.size_grid_layout, parent, false) as TextView
        } else {
            convertView as TextView
        }

        // Set the size text
        view.text = sizes.size

        // Add click listener to handle selection
        view.setOnClickListener {
            toggleSelection(position)
        }

        // Highlight the selected item
        if (selectedItems.contains(position)) {
            view.setBackgroundResource(0) // Set your selected background drawable here
        } else {
            view.setBackgroundResource(0) // Set your default background drawable here
        }

        return view
    }

    private fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }

        notifyDataSetChanged()
    }
}