package com.varsitycollege.mediaspace.data

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.ui.ViewProductActivity


class SizeAdapter(private val sizes: List<Size>) : BaseAdapter() {

    private val selectedItems = HashSet<Int>() // Keep track of selected items

    override fun getCount(): Int {
        return sizes.size
    }

    override fun getItem(position: Int): Any {
        return sizes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val size = sizes[position]

        val view: ConstraintLayout = if (convertView == null) {
            val inflater = LayoutInflater.from(parent?.context)
            inflater.inflate(R.layout.size_grid_layout, parent, false) as ConstraintLayout
        } else {
            convertView as ConstraintLayout
        }

        val sizeTextView: TextView = view.findViewById(R.id.sizeView)

        // Set the size text
        sizeTextView.text = size.size

        // Check if the size is available
        if (!size.available!!) {
            // Set the background tint list if not available
            sizeTextView.setBackgroundResource(R.drawable.edit_text_border_unavailable)
            sizeTextView.isClickable = false // Disable click on unavailable sizes
            sizeTextView.paintFlags = sizeTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            sizeTextView.backgroundTintList = null // Reset background tint if available
            sizeTextView.isClickable = true // Enable click on available sizes
            if (selectedItems.contains(position)) {
                sizeTextView.setBackgroundResource(R.drawable.edit_text_border)
                //callback.onSizeSelected(size.size.toString())
            } else {
                sizeTextView.setBackgroundResource(R.drawable.edit_text_border_unselected)
            }
        }

        // Add click listener to handle selection
        sizeTextView.setOnClickListener {
            toggleSelection(position)
            notifyDataSetChanged() // Notify the adapter that the data set has changed
        }

        return view
    }

    private fun toggleSelection(position: Int) {
        selectedItems.clear() // Clear the set before adding the new selection
        selectedItems.add(position)
    }
}
