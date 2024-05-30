package com.varsitycollege.mediaspaceadmin

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.FirebaseDatabase
import com.varsitycollege.mediaspaceadmin.data.Order
import com.varsitycollege.mediaspaceadmin.data.OrderAdapter
import com.varsitycollege.mediaspaceadmin.data.User
import com.varsitycollege.mediaspaceadmin.databinding.ActivityUpdateOrderBinding
import com.varsitycollege.mediaspaceadmin.databinding.FragmentManageOrdersBinding
import com.varsitycollege.mediaspaceadmin.ui.ManageOrdersFragment

class UpdateOrderActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUpdateOrderBinding
    private lateinit var database: FirebaseDatabase
    private var order = Order()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        order = intent.getSerializableExtra("order") as Order? ?: Order()

        if (order.deliveryId != null) {
            binding.textInputLayoutUserId.text = "User ID: " + order.customerId
            binding.textInputLayoutOrderStatus.text = "Order Status: " + order.status
            binding.textInputLayoutOrderDate.text = "Order Date: " + order.date
            binding.textInputLayoutDeliveryId.text = "Order Num: " + order.orderNum
        }

        // custom item adapter
        val items = arrayOf("Pending", "Paid", "Shipped", "Delivered")
        var autoCompleteTextView = binding.autoCompleteTextView as MaterialAutoCompleteTextView
        val adapter = ArrayAdapter(applicationContext, R.layout.layout_list_item, items)
        autoCompleteTextView.setAdapter(adapter)

        //on item clicked listener for autoCompleteTextView
        //https://copyprogramming.com/howto/how-to-add-listener-to-autocompletetextview-android
        //accessed 27 November 2023
        autoCompleteTextView.setOnItemClickListener { adapterView, view, position, l ->
            val value = adapter.getItem(position) ?: "Pending"
            order.status = value
            updateStatus()
        }

        //go back home
        binding.goBackButton.setOnClickListener {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun updateStatus() {
        val userRef = database.getReference("users")
        //Get user data
        userRef.get()
            .addOnSuccessListener {
                for (u in it.children) {
                    val user = u.getValue(User::class.java)
                    if (user != null) {
                        //If user data is current user, update their order status
                        if (user.id == order.customerId) {
                            for ((count, o) in user.orderHistory!!.withIndex()) {
                                //Update order for selected status
                                if (o.deliveryId == order.deliveryId) {
                                    userRef.child(order.customerId!!).child("orderHistory").child(count.toString()).child("status").setValue(order.status)
                                        .addOnSuccessListener {
                                            Toast.makeText(applicationContext, "Successfully updated order ${order.deliveryId}.", Toast.LENGTH_LONG).show()
                                            Log.i("Success","Successfully updated order ${order.deliveryId}.")
                                            binding.textInputLayoutOrderStatus.text = "Order Status: " + order.status
                                        }
                                        .addOnFailureListener {
                                            Log.e("Error","Failed to update order ${order.deliveryId}.")
                                        }
                                }
                            }
                        }

                    }
                    else {
                        Log.e("Error","Failed to find user.")
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Error","Failed to get user order data.")
            }
    }

}