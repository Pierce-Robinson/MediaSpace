package com.varsitycollege.mediaspace.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.android.material.textview.MaterialTextView
import com.varsitycollege.mediaspace.R

class ColourAdapter(private val context: Context, private val product: Product) : BaseAdapter() {

    private val colourList: List<Colour> = product.colourList.orEmpty()
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
        var itemView = convertView
        if (itemView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(R.layout.grid_item_layout, null)
        }

        // Bind your data to the views in the grid item layout
        val colourView = itemView?.findViewById<View>(R.id.colourCard)
        colourView?.setBackgroundColor(android.graphics.Color.parseColor(("#" + colourList[position].colour)))

        // Set a click listener to toggle selection
        colourView?.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                // Deselect logic (e.g., change color or do something else)
                colourView?.setBackgroundColor(android.graphics.Color.parseColor(("#" + colourList[position].colour)))
                val check = itemView?.findViewById<View>(R.id.checkIcon)
                check?.visibility = View.INVISIBLE
            } else {
                selectedItems.add(position)
                // Select logic (e.g., change color or do something else)
                val check = itemView?.findViewById<View>(R.id.checkIcon)
                check?.visibility = View.VISIBLE

            }
        }

        return itemView!!
    }
}