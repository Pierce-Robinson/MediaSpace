package com.varsitycollege.mediaspace.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.databinding.FragmentTrendingBinding

class TrendingFragment : Fragment() {

    private var adapter: ProductAdapter? = null
    private var _binding: FragmentTrendingBinding? = null
    private val binding get() = _binding!!

    private var productList: ArrayList<Product> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        ref = FirebaseDatabase.getInstance().reference.child("products")

        //fetchDataFromFirebase()
        getProducts()
        updateRecyclerView(productList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        productRecyclerView = binding.recyclerViewTrending
        productAdapter = ProductAdapter(ArrayList(), product = null) // initialize with an empty list
        productRecyclerView.layoutManager = LinearLayoutManager(context)
        productRecyclerView.adapter = productAdapter
    }

    private fun updateRecyclerView(newProductList: ArrayList<Product>) {
        productAdapter.productList = newProductList
        productAdapter.notifyDataSetChanged()
    }

    private fun getProducts() {
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        ref = database.getReference("products")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Clear old data
                    productList.clear()
                    for (p in snapshot.children) {
                        val product = p.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get products from database: " + error.message)
            }
        })
    }


//    private fun fetchDataFromFirebase() {
//        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
//        ref = database.getReference("products")
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val productList = mutableListOf<Product>()
//                for (snapshot in dataSnapshot.children) {
//                    val product = snapshot.getValue(Product::class.java)
//                    product?.let { productList.add(it) }
//                }
//                Log.d(TAG, "Received ${productList.size} products from Firebase")
//                updateRecyclerView(productList)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//
//                Log.e(TAG, "Firebase Database Error: ${databaseError.message}")
//
//            }
//        })
//    }

    private fun updateRecyclerView(productList: List<Product>) {
        adapter?.updateData(productList)
    }
}
