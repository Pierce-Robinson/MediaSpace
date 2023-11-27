package com.varsitycollege.mediaspace.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.HomeViewModel
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.databinding.FragmentCategoriesBinding
import com.varsitycollege.mediaspace.databinding.FragmentSingleCategoryBinding

class SingleCategoryFragment : Fragment() {

    private var _binding: FragmentSingleCategoryBinding? = null
    //Access shared view model
    //https://stackoverflow.com/questions/69059497/how-can-i-access-a-shared-view-model-from-an-activity
    //accessed 27 November 2023
    private val model: HomeViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productRecyclerView: RecyclerView

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleCategoryBinding.inflate(inflater, container, false)

        //Toast.makeText(this@SingleCategoryFragment.context, "${model.currentCategory.value}", Toast.LENGTH_LONG).show()

        // Assuming you have a RecyclerView with the ID R.id.recyclerViewTrending in your layout
        productRecyclerView = binding.recyclerViewCategoryData

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

        return binding.root
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
                        if (product?.categoriesList != null && product.categoriesList.contains(model.currentCategory.value)) {
                            newProducts.add(product)
                        }
                    }

                    // Update the adapter with new data
                    productAdapter.setProducts(newProducts)
                    //Update UI
                    binding.categoryTitle.text = model.currentCategory.value
                    binding.progressBar.visibility = View.GONE
                    binding.categoryTitle.visibility = View.VISIBLE
                    binding.solidLine1.visibility = View.VISIBLE
                    binding.recyclerViewCategoryData.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get products from database: ${error.message}")
            }
        })
    }

}