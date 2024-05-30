package com.varsitycollege.mediaspaceadmin.data

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.varsitycollege.mediaspaceadmin.R

class CategoryAdapter (private val categoryList: ArrayList<String>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return  CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryList[position]

        holder.text.text = currentItem

        holder.card.setOnClickListener {
            categoryList.removeAt(position)
            //refresh the recycler view
            //https://stackoverflow.com/questions/38000302/how-do-i-update-recyclerview-inside-onbindviewholder-of-its-adapter
            //accessed 20 November 2023
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, categoryList.size)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
    class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: LinearLayoutCompat = itemView.findViewById(R.id.categoryCard)
        val text: TextView = itemView.findViewById(R.id.categoryText)
    }

}