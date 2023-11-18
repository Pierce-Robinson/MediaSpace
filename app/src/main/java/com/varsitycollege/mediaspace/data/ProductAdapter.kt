package com.varsitycollege.mediaspace.data

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.varsitycollege.mediaspace.R
class ProductAdapter (private val productList: ArrayList<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ProductViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return  ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {

        val currentItem = productList[position]
        holder.productTitle.text = currentItem.name
        holder.productPrice.text = "R ${currentItem.price}"
        //TODO: put images into image view
    }

    override fun getItemCount(): Int {
        return productList.size
    }
    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: TextView = itemView.findViewById(R.id.productImage)
    }

}