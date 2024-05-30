package com.varsitycollege.mediaspaceadmin.data

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.database.FirebaseDatabase
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.varsitycollege.mediaspaceadmin.R

class SizeStockAdapter (private val sizeList: ArrayList<Size>, private val sku: String): RecyclerView.Adapter<SizeStockAdapter.SizeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.colour_stock_item, parent, false)
        return  SizeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val currentItem = sizeList[position]

        holder.name.text = currentItem.size
        holder.value.visibility = GONE
        holder.switch.isChecked = currentItem.available!!

        holder.switch.setOnClickListener {
            val database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
            val ref = database.getReference("products")
            ref.get().addOnSuccessListener {
                for (p in it.children) {
                    val product = p.getValue(Product::class.java)
                    if (product != null) {
                        if (product.sku.equals(sku)) {
                            //Update size availability
                            ref.child(p.key!!).child("sizeList").child("$position").child("available").setValue(holder.switch.isChecked)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return sizeList.size
    }
    class SizeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card: CardView = itemView.findViewById(R.id.colourContainer)
        val name: TextView = itemView.findViewById(R.id.colourName)
        val value: TextView = itemView.findViewById(R.id.colourValue)
        val switch: MaterialSwitch = itemView.findViewById(R.id.colourSwitch)
    }

}