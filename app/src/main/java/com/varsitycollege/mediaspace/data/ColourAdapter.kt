package com.varsitycollege.mediaspace.data

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.textview.MaterialTextView
import com.varsitycollege.mediaspace.R

class ColourAdapter(private val colourList: List<Colour>) : BaseAdapter() {

    private val selectedItems = HashSet<Int>() // Keep track of selected items

    override fun getCount(): Int {
        return colourList.size
    }

    override fun getItem(position: Int): Any {
        return colourList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var colours = colourList[position]
        var itemView = convertView
        if (itemView == null) {
            val inflater = LayoutInflater.from(parent?.context)
            itemView = inflater.inflate(R.layout.grid_item_layout, parent, false)
        }

        // Bind your data to the views in the grid item layout
        val colourView = itemView?.findViewById<View>(R.id.colourCard)
        val backgroundL = itemView?.findViewById<View>(R.id.LinearBack)


        if (!colours.available!!) {
            // Set the background tint list if not available
            backgroundL?.setBackgroundResource(R.drawable.edit_text_border_unavailable)
            colourView?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#" + colourList[position].colour))
            val cross = itemView?.findViewById<View>(R.id.iconX)
            val check = itemView?.findViewById<View>(R.id.checkIcon)
            check?.visibility = View.GONE
            cross?.visibility = View.VISIBLE
            colourView?.isClickable = true
            backgroundL?.isClickable = true // Disable click on unavailable colors
            backgroundL?.isFocusable = true // Disable focus on unavailable colors
        } else {
            backgroundL?.setBackgroundResource(R.drawable.edit_text_border_unselected)
            colourView?.isClickable = false // Enable click on available colors
            colourView?.isClickable = false // Enable click on available colors
            backgroundL?.isClickable = false // Enable click on available colors
            backgroundL?.isFocusable = false // Enable focus on available colors
            // Highlight the selected item
            if (selectedItems.contains(position)) {
                colourView?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#" + colourList[position].colour))
                itemView?.findViewById<View>(R.id.LinearBack)
                val check = itemView?.findViewById<View>(R.id.checkIcon)
                backgroundL?.setBackgroundResource(R.drawable.edit_text_border)
                check?.visibility = View.VISIBLE
            } else {
                colourView?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#" + colourList[position].colour))
                val check = itemView?.findViewById<View>(R.id.checkIcon)
                backgroundL?.setBackgroundResource(R.drawable.edit_text_border_unselected)
                check?.visibility = View.GONE
            }
        }


        colourView?.setOnClickListener {
            toggleSelection(position)
            notifyDataSetChanged() // Notify the adapter that the data set has changed
        }




        return itemView!!
    }
    private fun toggleSelection(position: Int) {
        selectedItems.clear() // Clear the set before adding the new selection
        selectedItems.add(position)
    }
}
