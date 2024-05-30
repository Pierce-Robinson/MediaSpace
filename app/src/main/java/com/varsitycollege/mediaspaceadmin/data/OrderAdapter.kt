package com.varsitycollege.mediaspaceadmin.data

import android.content.Intent
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
import com.varsitycollege.mediaspaceadmin.UpdateOrderActivity

class OrderAdapter (private val orderList: ArrayList<Order>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return  OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem = orderList[position]

        holder.number.text = currentItem.orderNum
        holder.status.text = currentItem.status
        holder.name.text = currentItem.customerId

        holder.card.setOnClickListener {
            // Go to manage order page
            //currentItem.customProductsList = arrayListOf()
            val intent = Intent(holder.itemView.context, UpdateOrderActivity::class.java)
                .putExtra("order", currentItem)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
    class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById(R.id.orderContainer)
        val number: TextView = itemView.findViewById(R.id.orderNumber)
        val status: TextView = itemView.findViewById(R.id.orderStatus)
        val name: TextView = itemView.findViewById(R.id.customerName)
    }

}