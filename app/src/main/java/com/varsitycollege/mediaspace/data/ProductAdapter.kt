package com.varsitycollege.mediaspace.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.ui.ProductsByCategoryFragment
import com.varsitycollege.mediaspace.ui.ViewProductActivity

//interface OnProductClickListener {
//    fun onProductClick(product: Product)
//}
class ProductAdapter(private var productList: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


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
                       .putExtra("sku", selectedProduct.sku)
                       .putExtra("name", selectedProduct.name)
                       .putExtra("description", selectedProduct.description)
                       .putExtra("price", selectedProduct.price)
                       .putExtra("stock", selectedProduct.stock)
                       .putExtra("colours", selectedProduct.colourList)
                       .putExtra("categories", selectedProduct.categoriesList)
                       .putExtra("sizes", selectedProduct.sizeList)
                       .putExtra("images", selectedProduct.imagesList)
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

        for (i in currentItem.imagesList!!){
            val imageUrls = currentItem.imagesList ?: emptyList()
            val imagePagerAdapter = ImagePagerAdapter(imageUrls)
            holder.viewPager.adapter = imagePagerAdapter
        }


       // Set up dots indicator
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