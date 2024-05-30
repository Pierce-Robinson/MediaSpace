package com.varsitycollege.mediaspaceadmin.data

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.varsitycollege.mediaspaceadmin.R

class ColourStockAdapter (private val colourList: ArrayList<Colour>, private val sku: String): RecyclerView.Adapter<ColourStockAdapter.ColourViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColourViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.colour_stock_item, parent, false)
        return  ColourViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        val currentItem = colourList[position]

        holder.name.text = currentItem.name
        holder.value.text = currentItem.colour
        holder.switch.isChecked = currentItem.available!!

        holder.switch.setOnClickListener {
            val database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
            val ref = database.getReference("products")
            ref.get().addOnSuccessListener {
                for (p in it.children) {
                    val product = p.getValue(Product::class.java)
                    if (product != null) {
                        if (product.sku.equals(sku)) {
                            //Update colour availability
                            ref.child(p.key!!).child("colourList").child("$position").child("available").setValue(holder.switch.isChecked)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return colourList.size
    }
    class ColourViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById(R.id.colourContainer)
        val name: TextView = itemView.findViewById(R.id.colourName)
        val value: TextView = itemView.findViewById(R.id.colourValue)
        val switch: MaterialSwitch = itemView.findViewById(R.id.colourSwitch)
    }

}