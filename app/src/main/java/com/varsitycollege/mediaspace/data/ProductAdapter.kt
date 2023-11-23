package com.varsitycollege.mediaspace.data

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.varsitycollege.mediaspace.R


class ProductAdapter(var productList: ArrayList<Product>, private val product: Product?) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    private val imageStrings: List<String> = product?.imagesList.orEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val currentItem = productList[position]
        holder.productTitle.text = currentItem.name
        holder.productPrice.text = "R ${currentItem.price}"
        //TODO: put images into image view


        val imageUrls = listOf(imageStrings[position], imageStrings[position])
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
        return productList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.trendingProductImage)
    }

    fun updateData(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }
}