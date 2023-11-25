package com.varsitycollege.mediaspace.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.mediaspace.data.ProductAdapter
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.data.Product
import com.google.firebase.database.*
import com.varsitycollege.mediaspace.databinding.FragmentProductsByCategoryBinding


class ProductsByCategoryFragment : Fragment() {

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String): ProductsByCategoryFragment {
            val fragment = ProductsByCategoryFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentProductsByCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productsRecyclerView: RecyclerView
    private var category: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsByCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        category = arguments?.getString(ARG_CATEGORY)

        // Assuming you have a RecyclerView with the ID R.id.recyclerViewProductsCategories in your layout
        productsRecyclerView = binding.recyclerViewProductsCategories

        // Initialize the adapter with the click listener
        //productAdapter = ProductAdapter(ArrayList(), this)

        // Set the adapter on the RecyclerView
        productsRecyclerView.layoutManager = LinearLayoutManager(context)
        productsRecyclerView.adapter = productAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        ref = database.getReference("products")

        // Fetch products in the selected category from Firebase
        getProductsInCategory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProductsInCategory() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val productsInCategory = ArrayList<Product>()

                    for (p in snapshot.children) {
                        val product = p.getValue(Product::class.java)
                        product?.let {
                            if (it.categoriesList?.contains(category) == true) {
                                productsInCategory.add(it)
                            }
                            if (category.isNullOrEmpty()) {
                                Log.e("ProductsByCategoryFragment", "Category is null or empty.")
                                // Handle the case where category is null or empty, maybe show an error message
                                return
                            }
                        }
                    }

                    // Update the adapter with products in the selected category
                    productAdapter.setProducts(productsInCategory)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get products from database: ${error.message}")
            }
        })
    }
    private fun getProductsForCategory() {
        // Initialize Firebase
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        ref = database.getReference("products")

        // TODO: Fetch products for the selected category from Firebase
        // Update the productAdapter with the list of products
    }
}

