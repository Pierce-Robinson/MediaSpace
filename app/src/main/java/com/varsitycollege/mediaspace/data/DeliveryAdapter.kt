package com.varsitycollege.mediaspace.data

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.DeliveryActivity
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.ui.ViewProductActivity

class DeliveryAdapter (private val deliveryList: ArrayList<Delivery>): RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return  DeliveryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        val currentItem = deliveryList[position]

        if (currentItem.id.equals("add")) {
            holder.content.visibility = GONE
            holder.add.visibility = VISIBLE

            holder.add.setOnClickListener {
                // Launch the DeliveryActivity to add a new delivery address
                val intent = Intent(holder.itemView.context, DeliveryActivity::class.java)

                holder.itemView.context.startActivity(intent)
            }
        }
        else {
            holder.city.text = currentItem.city
            holder.lines.text = currentItem.addressLineOne + ", " + currentItem.addressLineTwo

            holder.button.setOnClickListener {
                // Launch the DeliveryActivity, passing necessary data for updating
                val intent = Intent(holder.itemView.context, DeliveryActivity::class.java)
                    .putExtra("delivery", currentItem)
                holder.itemView.context.startActivity(intent)
            }

            holder.delete.setOnClickListener {
                //Confirm address deletion
                MaterialAlertDialogBuilder(holder.itemView.context)
                    .setTitle("Delete Address")
                    .setMessage("Are you sure you want to delete ${currentItem.addressLineOne}, ${currentItem.addressLineTwo} (${currentItem.city})?")
                    .setPositiveButton("Yes") { _, _ ->
                        val database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
                        val ref = database.getReference("users")
                        ref.get().addOnSuccessListener {
                            for (p in it.children) {
                                val user = p.getValue(User::class.java)
                                if (user != null) {
                                    if (user.id.equals(currentItem.customerId)) {
                                        //Delete address
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
        }

    }

    override fun getItemCount(): Int {
        return deliveryList.size
    }
    class DeliveryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val button: LinearLayoutCompat = itemView.findViewById(R.id.deliveryButton)
        val city: TextView = itemView.findViewById(R.id.deliveryCity)
        val lines: TextView = itemView.findViewById(R.id.deliveryLines)
        val delete: ImageView = itemView.findViewById(R.id.deliveryDelete)
        val add: MaterialButton = itemView.findViewById(R.id.addDeliveryButton)
        val content: LinearLayoutCompat = itemView.findViewById(R.id.deliveryContent)

    }

}