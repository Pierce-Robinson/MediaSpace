package com.varsitycollege.mediaspaceadmin.data

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.varsitycollege.mediaspaceadmin.ManageStockActivity
import com.varsitycollege.mediaspaceadmin.R
import com.varsitycollege.mediaspaceadmin.UpdateProductActivity

class ProductAdapter (private val productList: ArrayList<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return  ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]

        holder.sku.text = currentItem.sku
        holder.name.text = currentItem.name

        holder.update.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateProductActivity::class.java)
                .putExtra("sku", currentItem.sku)
                .putExtra("name", currentItem.name)
                .putExtra("description", currentItem.description)
                .putExtra("price", currentItem.price)
                .putExtra("stock", currentItem.stock)
                .putExtra("colours", currentItem.colourList)
                .putExtra("categories", currentItem.categoriesList)
                .putExtra("images", currentItem.imagesList)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        holder.colour.setOnClickListener {
            val intent = Intent(holder.itemView.context, ManageStockActivity::class.java)
                .putExtra("sku", currentItem.sku)
                .putExtra("mode", "colours")
                .putExtra("colours", currentItem.colourList)
                .putExtra("sizes", currentItem.sizeList)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        holder.size.setOnClickListener {
            val intent = Intent(holder.itemView.context, ManageStockActivity::class.java)
                .putExtra("sku", currentItem.sku)
                .putExtra("mode", "sizes")
                .putExtra("colours", currentItem.colourList)
                .putExtra("sizes", currentItem.sizeList)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }

        holder.delete.setOnClickListener {
            //Confirm product deletion
            MaterialAlertDialogBuilder(holder.itemView.context)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete ${currentItem.sku} (${currentItem.name})?")
                .setPositiveButton("Yes") { _, _ ->
                    val database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
                    val ref = database.getReference("products")
                    ref.get().addOnSuccessListener {
                        for (p in it.children) {
                            val product = p.getValue(Product::class.java)
                            if (product != null) {
                                if (product.sku.equals(currentItem.sku)) {
                                    //Delete product images
                                    if (product.imagesList != null) {
                                        for (i in product.imagesList) {
                                            val test = FirebaseStorage.getInstance().getReferenceFromUrl(i)
                                            test.delete()
                                                .addOnSuccessListener {
                                                    Log.i("Image deleted", "Successfully deleted ${test.name}")
                                                }
                                                .addOnFailureListener {
                                                    Log.e("Image delete fail", "Failed to delete ${test.name}")
                                                }
                                        }
                                    }

                                    //Delete product
                                    p.key?.let { it1 ->
                                        ref.child(it1).removeValue()
                                            .addOnSuccessListener {
                                                Log.i("Product deleted", "Successfully deleted ${currentItem.sku}")
                                            }
                                            .addOnFailureListener {
                                                Log.e("Product delete fail", "Failed to delete ${currentItem.sku}")
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

    }

    override fun getItemCount(): Int {
        return productList.size
    }
    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sku: TextView = itemView.findViewById(R.id.productSKU)
        val name: TextView = itemView.findViewById(R.id.productName)
        val update: LinearLayoutCompat = itemView.findViewById(R.id.productButton)
        val delete: ImageView = itemView.findViewById(R.id.productDelete)
        val colour: MaterialButton = itemView.findViewById(R.id.colourButton)
        val size: MaterialButton = itemView.findViewById(R.id.sizeButton)
    }

}