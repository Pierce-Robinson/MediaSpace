package com.varsitycollege.mediaspaceadmin.data

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.varsitycollege.mediaspaceadmin.R

class ImagesAdapter (private val imagesList: ArrayList<Uri>): RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.images_item, parent, false)
        return  ImagesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val currentItem = imagesList[position]

        holder.background.setImageURI(currentItem)

        holder.delete.setOnClickListener {
            imagesList.removeAt(position)
            //refresh the recycler view
            //https://stackoverflow.com/questions/38000302/how-do-i-update-recyclerview-inside-onbindviewholder-of-its-adapter
            //accessed 20 November 2023
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, imagesList.size)
        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }
    class ImagesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById(R.id.imageCard)
        val background: ImageView = itemView.findViewById(R.id.imageBackground)
        val delete: ImageView = itemView.findViewById(R.id.imageButton)
    }

}