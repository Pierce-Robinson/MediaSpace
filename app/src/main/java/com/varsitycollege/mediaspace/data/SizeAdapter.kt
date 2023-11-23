package com.varsitycollege.mediaspace.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.varsitycollege.mediaspace.R

class SizeAdapter(private val context: Context, private val sizes: List<String>) : BaseAdapter() {

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

        val view: View = if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.grid_item_size, parent, false)
        } else {
            convertView
        }

        val sizeView: TextView = view.findViewById(R.id.sizeView)

        // Set the size text
        sizeView.text = size

        return view
    }
}