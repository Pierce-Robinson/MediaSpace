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

    private var _binding: FragmentTrendingBinding? = null
    private val binding get() = _binding!!

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

        // Assuming you have a RecyclerView with the ID R.id.recyclerViewTrending in your layout
        productRecyclerView = binding.recyclerViewTrending

        // Initialize the adapter
        productAdapter = ProductAdapter(ArrayList())

        // Set the adapter on the RecyclerView
        productRecyclerView.layoutManager = LinearLayoutManager(context)
        productRecyclerView.adapter = productAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        ref = database.getReference("products")

        // Fetch data from Firebase
        getProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProducts() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newProducts = ArrayList<Product>()

                    for (p in snapshot.children) {
                        val product = p.getValue(Product::class.java)
                        product?.let { newProducts.add(it) }
                    }

                    // Update the adapter with new data
                    productAdapter.setProducts(newProducts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get products from database: ${error.message}")
            }
        })
    }
}