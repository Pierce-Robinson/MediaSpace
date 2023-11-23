package com.varsitycollege.mediaspace.data
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.varsitycollege.mediaspace.R

class ColourAdapter (private val context: Context, private val colors: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return colors.size
    }

    override fun getItem(position: Int): Any {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val color = colors[position]

        val view: View = if (convertView == null) {
            // If the view is not recycled, inflate the layout
            val inflater = LayoutInflater.from(context)
            inflater.inflate(R.layout.grid_item_color, parent, false)
        } else {
            convertView
        }

        val cardView: CardView = view.findViewById(R.id.cardViewColor)
        val colorView: View = view.findViewById(R.id.colorView)

        // Convert hex color code to actual color
        val colorInt = android.graphics.Color.parseColor(color)

        // Set the background color of the colorView
        colorView.setBackgroundColor(colorInt)

        return view
    }
}