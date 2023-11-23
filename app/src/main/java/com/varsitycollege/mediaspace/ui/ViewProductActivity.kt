package com.varsitycollege.mediaspace.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.BuildConfig.rtdb_conn
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.ColourAdapter
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.data.SizeAdapter

class ViewProductActivity : AppCompatActivity() {

    private lateinit var gridViewColors: GridView
    private lateinit var gridViewSizes: GridView
    private lateinit var colorAdapter: ColourAdapter
    private lateinit var sizeAdapter: SizeAdapter

    private var _binding: ViewProductActivity? = null
    private var productList: ArrayList<Product> = arrayListOf()

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        gridViewColors = findViewById(R.id.colourGrid)
        gridViewSizes = findViewById(R.id.sizeGrid)

        // Initialize adapters
//        colorAdapter = ColourAdapter(this, mutableListOf())
//        sizeAdapter = SizeAdapter(this, mutableListOf())

        // Set adapters for grid views
        gridViewColors.adapter = colorAdapter
        gridViewSizes.adapter = sizeAdapter

        // Fetch data from Firebase
        //getProducts()
    }

//    private fun getProducts() {
//        database = FirebaseDatabase.getInstance(rtdb_conn)
//        ref = database.getReference("products")
//
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    // Clear old data
//                    productList.clear()
//                    for (p in snapshot.children) {
//                        val product = p.getValue(Product::class.java)
//                        if (product != null) {
//                            productList.add(product)
//                        }
//                    }
//
//                    //TODO: daniel ive sorted the manage orders by alphabetical order by SKU
//                    //sort productList by SKU
//                    productList.sortBy { it.sku }
//
//                    // Populate view
//                    val sizeAdapter = SizeAdapter(productList)
//                    val colourAdapter = ColourAdapter(productList)
//                    if (_binding != null) {
//                        binding.productRecycler.adapter = adapter
//                        binding.progressBar.visibility = View.GONE
//                        binding.searchView.visibility = View.VISIBLE
//                        binding.productRecycler.visibility = View.VISIBLE
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("Database error", "Failed to get products from database: " + error.message)
//            }
//        })
//    }
}
