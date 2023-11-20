package com.varsitycollege.mediaspace.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.varsitycollege.mediaspace.R

class CartAdapter(private val context: Context, private val dataList: List<Order>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]

        // Bind data to views
//        holder.productNameTextView.text = currentItem.productName
//        holder.productDescriptionTextView.text = currentItem.productDescription
        // ... bind other views

        // Add click listeners or any other custom logic here
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: MaterialTextView = itemView.findViewById(R.id.productName)
        val productDescriptionTextView: MaterialTextView = itemView.findViewById(R.id.productDescription)
        // ... initialize other views
    }
}
