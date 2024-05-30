package com.varsitycollege.mediaspaceadmin.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.varsitycollege.mediaspaceadmin.R
import com.varsitycollege.mediaspaceadmin.data.Product
import com.varsitycollege.mediaspaceadmin.data.ProductAdapter
import com.varsitycollege.mediaspaceadmin.databinding.FragmentDashboardBinding
import com.varsitycollege.mediaspaceadmin.databinding.FragmentManageProductsBinding
import org.checkerframework.checker.index.qual.GTENegativeOne

class ManageProductsFragment : Fragment() {

    private var _binding: FragmentManageProductsBinding? = null
    private var productList: ArrayList<Product> = arrayListOf()

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageProductsBinding.inflate(inflater, container, false)

        binding.productRecycler.layoutManager = LinearLayoutManager(this@ManageProductsFragment.context)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText ?: "")
                return true
            }
        })

        getProducts()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun filterProducts(query: String) {
        val filteredList = if (query.isEmpty()) {
            productList
        } else {
            productList.filter { product ->
                product.sku!!.contains(query, ignoreCase = true)
            }
        }
        val filteredArray = arrayListOf<Product>()
        filteredArray.addAll(filteredList)
        val adapter = ProductAdapter(filteredArray)
        binding.productRecycler.adapter = adapter
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

                    //sort productList by SKU
                    productList.sortBy { it.sku }

                    // Populate view
                    val adapter = ProductAdapter(productList)
                    if (_binding != null) {
                        binding.productRecycler.adapter = adapter
                        binding.progressBar.visibility = GONE
                        binding.searchView.visibility = VISIBLE
                        binding.productRecycler.visibility = VISIBLE
                        binding.solidLine1.visibility = VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get products from database: " + error.message)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}