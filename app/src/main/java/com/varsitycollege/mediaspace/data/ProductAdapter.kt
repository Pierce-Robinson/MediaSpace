package com.varsitycollege.mediaspace.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.ui.ViewProductActivity

//interface OnProductClickListener {
//    fun onProductClick(product: Product)
//}
class ProductAdapter(private var productList: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {


    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.trendingProductImage)
        val productContainer: CardView = itemView.findViewById(R.id.productContainer)
        val trendingProductImage: ViewPager2 = itemView.findViewById(R.id.trendingProductImage)

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

        holder.productContainer.setOnClickListener {
            // Ensure the position is valid
            val selectedProduct = productList[position]

            // Launch the ProductActivity here, passing necessary data
            val intent = Intent(holder.itemView.context, ViewProductActivity::class.java)
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
            holder.itemView.context.startActivity(intent)
        }


        for (i in currentItem.imagesList!!) {
            val imageUrls = currentItem.imagesList
            val imagePagerAdapter = ImagePagerAdapter(imageUrls, productList, position)
            holder.viewPager.adapter = imagePagerAdapter
        }
    }


    override fun getItemCount(): Int {
        return productList.size
    }
}