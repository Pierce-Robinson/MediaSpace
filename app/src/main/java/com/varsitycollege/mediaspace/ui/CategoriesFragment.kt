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
import com.varsitycollege.mediaspace.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var categoriesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assuming you have a RecyclerView with the ID R.id.recyclerViewCategories in your layout
        categoriesRecyclerView = binding.recyclerViewCategories

        // Initialize the adapter
        categoriesAdapter = CategoriesAdapter(ArrayList())

        // Set the adapter on the RecyclerView
        categoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        categoriesRecyclerView.adapter = categoriesAdapter

        // Initialize Firebase
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        ref = database.getReference("categories")

        // Fetch data from Firebase
        getCategories()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCategories() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    //TODO: i believe i have to make a data class to get the categories? not sure this code is the same as trending
                    val newCategories = ArrayList<String>()

                    for (categorySnapshot in snapshot.children) {
                        val category = categorySnapshot.getValue(String::class.java)
                        category?.let { newCategories.add(it) }
                    }

                    Log.d("Categories", newCategories.toString())


                    categoriesAdapter.setCategories(newCategories)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error", "Failed to get categories from database: ${error.message}")
            }
        })
    }

}
