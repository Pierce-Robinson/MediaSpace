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
import com.varsitycollege.mediaspace.ui.ViewProductActivity


class ProductAdapter(private var productList: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var imageUrls  = arrayListOf<String>()

   inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.trendingProductImage)

       init {
           // Set an OnClickListener for the entire itemView (card)
           itemView.setOnClickListener {
               val position = adapterPosition
               if (position != RecyclerView.NO_POSITION) {
                   // Ensure the position is valid
                   val selectedProduct = productList[position]

                   // Launch the ProductActivity here, passing necessary data
                   val intent = Intent(itemView.context, ViewProductActivity::class.java)
                   intent.putExtra("PRODUCT_ID", selectedProduct.id)
                   // Add more data as needed
                   itemView.context.startActivity(intent)
               }
           }
       }
   }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }
    fun setProducts(newList: ArrayList<Product>) {
        productList = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.productTitle.text = currentItem.name
        holder.productPrice.text = "R ${currentItem.price}"
        //TODO: put images into image view

        for (i in currentItem.imagesList!!){
                imageUrls.add(i)
        }
        val imagePagerAdapter = ImagePagerAdapter(imageUrls)
        holder.viewPager.adapter = imagePagerAdapter




//        // Set up dots indicator
//        val dotsLayout = holder.itemView.findViewById<LinearLayout>(R.id.dotsLayout)
//        setupDots(imageUrls.size, dotsLayout, holder.viewPager)

    }



    override fun getItemCount(): Int {
        return productList.size
    }


//    private fun setupDots(count: Int, parent: LinearLayout, viewPager: ViewPager2) {
//        parent.removeAllViews()
//
//        val dots = arrayOfNulls<ImageView>(count)
//        for (i in 0 until count) {
//            dots[i] = ImageView(parent.context)
//            dots[i]?.setImageDrawable(
//                ContextCompat.getDrawable(
//                    parent.context,
//                    if (i == 0) R.drawable.active_dot else R.drawable.inactive_dot
//                )
//            )
//
//            val params = LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            params.setMargins(4, 0, 4, 0)
//            parent.addView(dots[i], params)
//        }
//
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                for (i in 0 until count) {
//                    dots[i]?.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            parent.context,
//                            if (i == position) R.drawable.active_dot else R.drawable.inactive_dot
//                        )
//                    )
//                }
//            }
//        })
//    }


}