package com.varsitycollege.mediaspace.data

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.ui.ViewProductActivity

class ImagePagerAdapter(
    private val imageUrls: List<String?>,
    private val productList: ArrayList<Product>,
    private val productPosition: Int

) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_gallery_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]

        // if you go to cart page it wont go to the product as well -- reason for the if isnotempty()
        if (productList.isNotEmpty()) {

            holder.imageView.setOnClickListener {
                val selectedProduct = productList[productPosition]

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
        }
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}