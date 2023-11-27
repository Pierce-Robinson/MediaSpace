package com.varsitycollege.mediaspace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.HomeActivity
import com.varsitycollege.mediaspace.OrderActivity
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.CartAdapter
import com.varsitycollege.mediaspace.data.CustomProduct
import com.varsitycollege.mediaspace.data.Order
import com.varsitycollege.mediaspace.data.OrderHistoryAdapter
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.ActivityOrderBinding
import com.varsitycollege.mediaspace.databinding.ActivityOrderHistoryBinding
import com.varsitycollege.mediaspace.databinding.ActivityViewProductBinding
import com.varsitycollege.mediaspace.databinding.FragmentCartBinding
import com.varsitycollege.mediaspace.databinding.FragmentSingleCategoryBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private var index = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        orderRecyclerView = binding.recyclerViewOrders
        orderHistoryAdapter = OrderHistoryAdapter(ArrayList()) // Initialize with an empty list
        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        orderRecyclerView.adapter = orderHistoryAdapter

        getOrdersForUser()
    }




    private fun getOrdersForUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val ordersRef = database.getReference("users").child(userId).child("orderHistory")

            //Attach a listener to retrieve the order items
            ordersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val orderList = ArrayList<Order>()

                        for (orderSnapshot in snapshot.children) {
                            Log.d("FirebaseDebug", "Order Snapshot: $orderSnapshot")
                            val orderNumber = orderSnapshot.child("orderNum").getValue(String::class.java)
                            val orderStatus = orderSnapshot.child("status").getValue(String::class.java)
                            val date = orderSnapshot.child("date").getValue(String::class.java)
                            val customerId = orderSnapshot.child("customerId").getValue(String::class.java)
                            val deliveryId = orderSnapshot.child("deliveryId").getValue(String::class.java)
                            val orderItems = ArrayList<CustomProduct>()
                            for (itemSnapshot in orderSnapshot.child("customProductsList").children) {
                                val orderItem = itemSnapshot.getValue(CustomProduct::class.java)
                                orderItem?.let { orderItems.add(it) }
                            }

                            val order = Order(orderNumber, date, orderStatus, customerId, deliveryId, orderItems)
                            orderList.add(order)
                            Log.d("FirebaseDebug", "Order List: $orderList")
                            orderHistoryAdapter.setOrderHistory(orderList)
                        }


                    } else {
                        Log.d("FirebaseDebug", "No orders found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase Error", "Failed to get orders: ${error.message}")
                }
            })
        }
    }

//    private fun getOrderHistory() {
//        auth = FirebaseAuth.getInstance()
//        val id = auth.currentUser?.uid
//        if (id != null) {
//            ref = database.getReference("users").child(id).child("orderHistory")
//            ref.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val newOrderHistory = ArrayList<Order>()
//
//                        for (p in snapshot.children) {
//                            val orderHistory = p.getValue(Order::class.java)
//                            orderHistory?.let { newOrderHistory.add(it) }
//                        }
//                        val total = newOrderHistory.sumOf { (it.price ?: 0.0) * (it.quantity ?: 0) }
//                        val roundedTotal = total.roundTo(2)
//
//                        // Update the adapter with new data
//                        if (newCart.isNotEmpty()) {
//                            cartAdapter.setOrders(newCart)
//                            if (_binding != null) {
//                                binding.progressBar.visibility = View.GONE
//                                binding.recyclerViewCart.visibility = View.VISIBLE
//                                binding.totalPriceTextView.text = "Total: R${roundedTotal}"
//                            }
//                        }
//                    }
//                    else {
//                        //Show a message or update UI to indicate an empty cart
//                        if (_binding != null) {
//                            binding.progressBar.visibility = View.GONE
//                            binding.recyclerViewCart.visibility = View.GONE
//                            binding.noCartItems.visibility = View.VISIBLE
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e(
//                        "Database error",
//                        "Failed to get products from database: ${error.message}"
//                    )
//                }
//            })
//        }
//    }

    private fun Double.roundTo(n: Int): String {
        return "%.${n}f".format(this)
    }
}
