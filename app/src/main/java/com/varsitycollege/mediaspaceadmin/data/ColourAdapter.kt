package com.varsitycollege.mediaspaceadmin.data

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.varsitycollege.mediaspaceadmin.R

class ColourAdapter (private val colourList: ArrayList<Colour>): RecyclerView.Adapter<ColourAdapter.ColourViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColourViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.colour_item, parent, false)
        return  ColourViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        val currentItem = colourList[position]
        val colour = Color.parseColor("#${currentItem.colour}")
        holder.card.backgroundTintList = ColorStateList.valueOf(colour)

        holder.delete.setOnClickListener {
            colourList.removeAt(position)
            //refresh the recycler view
            //https://stackoverflow.com/questions/38000302/how-do-i-update-recyclerview-inside-onbindviewholder-of-its-adapter
            //accessed 20 November 2023
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, colourList.size)
        }
    }

    override fun getItemCount(): Int {
        return colourList.size
    }
    class ColourViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById(R.id.colourCard)
        val delete: ImageView = itemView.findViewById(R.id.colourButton)
    }

}