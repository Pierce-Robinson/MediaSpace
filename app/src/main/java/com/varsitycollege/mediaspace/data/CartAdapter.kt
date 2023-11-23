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

class CartAdapter(private val context: Context, private var orders: List<CustomProduct>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewPager: ViewPager2 = itemView.findViewById(R.id.productImage)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val userInstructions: TextView = itemView.findViewById(R.id.userInstructions)
        val removeQuantity: Button = itemView.findViewById(R.id.removeQuantity)
        val addQuantity: Button = itemView.findViewById(R.id.addQuantity)
        val productQuantity: TextView = itemView.findViewById(R.id.qtyEditText)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val totalPrice: TextView = itemView.findViewById(R.id.totalTextView)



    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    fun setOrders(newList: List<CustomProduct>) {
        orders = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orders[position]

        holder.deleteButton.setOnClickListener(View.OnClickListener {

        })
        holder.productName.text = order.prodName
        holder.userInstructions.text = order.userInstructions

        holder.addQuantity.setOnClickListener(View.OnClickListener{

        })
        holder.productQuantity.text = order.quantity.toString()

        holder.removeQuantity.setOnClickListener(View.OnClickListener{

        })
        holder.productPrice.text = order.price.toString()
        holder.totalPrice.text = (order.price?.times(order.quantity?.toDouble()!!)).toString()


        //this is how we use the image pager
        val imageUrls = listOf(order.firstImage, order.design)
        val imagePagerAdapter = ImagePagerAdapter(imageUrls)
        holder.viewPager.adapter = imagePagerAdapter

        // Set up dots indicator
        val dotsLayout = holder.itemView.findViewById<LinearLayout>(R.id.dotsLayout)
        setupDots(imageUrls.size, dotsLayout, holder.viewPager)
    }


    private fun setupDots(count: Int, parent: LinearLayout, viewPager: ViewPager2) {
        parent.removeAllViews()

        val dots = arrayOfNulls<ImageView>(count)
        for (i in 0 until count) {
            dots[i] = ImageView(parent.context)
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    parent.context,
                    if (i == 0) R.drawable.active_dot else R.drawable.inactive_dot
                )
            )

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            parent.addView(dots[i], params)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until count) {
                    dots[i]?.setImageDrawable(
                        ContextCompat.getDrawable(
                            parent.context,
                            if (i == position) R.drawable.active_dot else R.drawable.inactive_dot
                        )
                    )
                }
            }
        })
    }
    override fun getItemCount(): Int {
        return orders.size
    }

}
