// CategoriesFragment.kt
package com.varsitycollege.mediaspace.ui

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
import com.varsitycollege.mediaspace.data.CategoriesAdapter
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var categoriesRecyclerView: RecyclerView
    private var newCategories = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        // Fetch data from Firebase
        getCategories()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCategories() {
        // Initialize Firebase
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        ref = database.getReference("products")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    newCategories.clear()
                    for (p in snapshot.children) {
                        val product = p.getValue(Product::class.java)
                        if (product?.categoriesList != null) {
                            for (c in product.categoriesList) {
                                if (!newCategories.contains(c)) {
                                    newCategories.add(c)
                                }
                            }

                        }
                    }
                    // Initialize the adapter
                    categoriesAdapter = CategoriesAdapter(newCategories)
                    // Set the adapter on the RecyclerView
                    binding.recyclerViewCategories.layoutManager = LinearLayoutManager(context)
                    binding.recyclerViewCategories.adapter = categoriesAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get categories from database: ${error.message}")
            }
        })
    }

}
