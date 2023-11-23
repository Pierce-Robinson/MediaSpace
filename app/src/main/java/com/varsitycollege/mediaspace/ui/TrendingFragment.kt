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
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.databinding.FragmentTrendingBinding

class TrendingFragment : Fragment() {
    private lateinit var productRecyclerView: RecyclerView
    private var adapter: ProductAdapter? = null
    private var _binding: FragmentTrendingBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseReference: DatabaseReference

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

        databaseReference = FirebaseDatabase.getInstance().reference.child("products")

        fetchDataFromFirebase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        productRecyclerView = binding.recyclerViewTrending
        productRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProductAdapter(ArrayList()) //initialise with empty list
        productRecyclerView.adapter = adapter
    }

    private fun fetchDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (snapshot in dataSnapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                Log.d(TAG, "Received ${productList.size} products from Firebase")
                updateRecyclerView(productList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

                Log.e(TAG, "Firebase Database Error: ${databaseError.message}")

            }
        })
    }

    private fun updateRecyclerView(productList: List<Product>) {
        adapter?.updateData(productList)
    }
}
