package com.varsitycollege.mediaspace.data

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.FirebaseDatabase
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.OrderActivity
import com.varsitycollege.mediaspace.R

class OrderHistoryAdapter(private var orderHistory: ArrayList<Order>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewPager: ViewPager2 = itemView.findViewById(R.id.productImageO)
        val orderNumber: TextView = itemView.findViewById(R.id.orderNumber)
        val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        val orderQuantity: TextView = itemView.findViewById(R.id.qtyEditTextO)
        val orderPrice: TextView = itemView.findViewById(R.id.priceEditTextO)
        val paymentInfo: Button = itemView.findViewById(R.id.paymentButton)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    fun setOrderHistory(newList: ArrayList<Order>) {
        orderHistory = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderHis = orderHistory[position]

        holder.orderNumber.text = "Order Number: \n${orderHis.orderNum}"
        holder.orderStatus.text = "Order Status: \n${orderHis.status}"
        val totalQuantity = orderHis.customProductsList!!.sumOf { it.quantity ?: 0 }
        holder.orderQuantity.text = "Quantity: $totalQuantity"
        val totalPrice = orderHis.customProductsList!!.sumOf { (it.price ?: 0.0) * (it.quantity ?: 0) }
        holder.orderPrice.text = "Total Price: R${"%.2f".format(totalPrice)}"

        holder.paymentInfo.setOnClickListener{
            val intent = Intent(holder.itemView.context, OrderActivity::class.java)
                .putExtra("orderNo", orderHis.orderNum)
            holder.itemView.context.startActivity(intent)
        }

        val images = ArrayList<String>()
        orderHis.customProductsList.forEach { customProduct ->
            customProduct.firstImage?.let { images.add(it) }
        }
        val imagePagerAdapter = ImagePagerAdapter(images, ArrayList(), position)
        holder.viewPager.adapter = imagePagerAdapter
    }
    override fun getItemCount(): Int {
        return orderHistory.size
    }

}
