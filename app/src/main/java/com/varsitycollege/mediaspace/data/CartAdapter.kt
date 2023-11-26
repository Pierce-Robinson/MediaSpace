package com.varsitycollege.mediaspace.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textview.MaterialTextView
import com.varsitycollege.mediaspace.R

class CartAdapter(private var orders: ArrayList<CustomProduct>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewPager: ViewPager2 = itemView.findViewById(R.id.productImage)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val userInstructions: TextView = itemView.findViewById(R.id.userInstructions)
        val productQuantity: TextView = itemView.findViewById(R.id.qtyEditTextC)
        val productPrice: TextView = itemView.findViewById(R.id.priceEditText)
        val totalPrice: TextView = itemView.findViewById(R.id.totalCEditText)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    fun setOrders(newList: ArrayList<CustomProduct>) {
        orders = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orders[position]

        holder.deleteButton.setOnClickListener(View.OnClickListener {

        })
        holder.productName.text = order.prodName
        holder.userInstructions.text = "Print Instructions: \n${order.userInstructions}"

        holder.productQuantity.text = "Quantity: ${order.quantity.toString()}"

        holder.productPrice.text = "Price: R${order.price.toString()}"
        holder.totalPrice.text =
            "Total: R${(order.price?.times(order.quantity?.toDouble()!!)).toString()}"


        //this is how we use the image pager
        var images = order.design?.toMutableList()
        if (images == null) {
            images = mutableListOf()
        }
        order.firstImage?.let { images.add(0, it) }
        val imagePagerAdapter = ImagePagerAdapter(images, ArrayList(), position)
        holder.viewPager.adapter = imagePagerAdapter
    }

    override fun getItemCount(): Int {
        return orders.size
    }

}
