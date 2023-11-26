package com.varsitycollege.mediaspace.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.CartAdapter
import com.varsitycollege.mediaspace.data.CustomProduct
import com.varsitycollege.mediaspace.data.Order
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.FragmentCartBinding
import com.varsitycollege.mediaspace.databinding.FragmentSingleCategoryBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private var _binding: FragmentCartBinding? = null
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private var index = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    //private lateinit var prodCartArrayList: ArrayList<Product>

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        cartRecyclerView = binding.recyclerViewCart
        cartAdapter = CartAdapter(ArrayList())



        cartRecyclerView.layoutManager = LinearLayoutManager(context)
        cartRecyclerView.adapter = cartAdapter

        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)

        binding.checkoutButton.setOnClickListener(){
            checkout()
        }

        getCart()
        return binding.root
    }

    private fun checkout() {


        val orderArray: ArrayList<Order> = arrayListOf()


        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val cartRef = database.getReference("users").child(userId).child("cart")
            val orderRef =
                database.getReference("users").child("orderHistory").child("" + index).push()
            val userRef = database.getReference("users").child(userId)

            userRef.get().addOnSuccessListener {
                val user = it.getValue(User::class.java)
                if (user != null) {
                    //Get any existing cart items
                    if (user.orderHistory != null) {
                        for (c in user.orderHistory) {
                            orderArray.add(c)
                        }
                    }
                    cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val productsInCart = ArrayList<CustomProduct>()

                                for (p in snapshot.children) {
                                    val customProduct = p.getValue(CustomProduct::class.java)
                                    customProduct?.let { productsInCart.add(it) }
                                }
//update index

                                val currentDate = LocalDate.now()
                                val formattedDate =
                                    currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

                                val order = Order(
                                    orderNum = "MS${System.currentTimeMillis()}",
                                    formattedDate,
                                    status = "Pending",
                                    userId,
                                    deliveryId = orderRef.key,
                                    productsInCart
                                )
                                orderRef.setValue(order)
                                //Add new cart item
                                orderArray.add(order)
                                index = orderArray.indexOf(Order())
                                userRef.child("orderHistory").setValue(orderArray)
                                    .addOnSuccessListener {
//                                        val intent =
//                                            Intent(context, HomeActivity::class.java)
//                                        startActivity(intent)
                                                                          }
                                    .addOnFailureListener { e ->
                                        Log.e("Error", "Failed to clear cart: ${e.message}")
                                    }
                                cartRef.removeValue().addOnSuccessListener {
                                }.addOnFailureListener { e ->
                                    Log.e("Checkout Error", "Failed to clear cart: ${e.message}")
                                }
                            }
                        }


                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Database error", "Failed to get cart items: ${error.message}")
                        }
                    })
                }
            }
        }

    }

    private fun getCart() {
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        if (id != null) {
            ref = database.getReference("users").child(id).child("cart")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val newCart = ArrayList<CustomProduct>()

                        for (p in snapshot.children) {
                            val customProduct = p.getValue(CustomProduct::class.java)
                            customProduct?.let { newCart.add(it) }
                        }
                        val total = newCart.sumOf { (it.price ?: 0.0) * (it.quantity ?: 0) }
                        val roundedTotal = total.roundTo(2)

                        // Update the adapter with new data
                        // Show a message or update UI to indicate an empty cart
                        if (newCart.isEmpty()) {
                            if (_binding != null) {
                                binding.recyclerViewCart.visibility = View.GONE
                                binding.noCartItems.visibility = View.VISIBLE
                            }

                        } else {
                            cartAdapter.setOrders(newCart)
                            binding.recyclerViewCart.visibility = View.VISIBLE
                            binding.noCartItems.visibility = View.GONE
                            if (_binding != null) {
                                binding.totalPriceTextView.text = "Total: R${roundedTotal}"
                            }
                        }
//                        //Update UI
//                        if (_binding != null) {
//                            binding.progressBar.visibility = View.GONE
//                            binding.categoryTitle.visibility = View.VISIBLE
//                            binding.solidLine1.visibility = View.VISIBLE
//                            binding.recyclerViewTrending.visibility = View.VISIBLE
//                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        "Database error",
                        "Failed to get products from database: ${error.message}"
                    )
                }
            })
        }
    }

    private fun Double.roundTo(n: Int): String {
        return "%.${n}f".format(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
