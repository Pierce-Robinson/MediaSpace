package com.varsitycollege.mediaspace.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.CartAdapter
import com.varsitycollege.mediaspace.data.CustomProduct
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.databinding.FragmentCartBinding
import com.varsitycollege.mediaspace.databinding.FragmentSingleCategoryBinding

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter
    private var _binding: FragmentCartBinding? = null
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

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


        getCart()
        return binding.root
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
                        if (newCart.isEmpty()) {
                            // Show a message or update UI to indicate an empty cart
                        } else {
                            cartAdapter.setOrders(newCart)
                                if(_binding != null){
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
