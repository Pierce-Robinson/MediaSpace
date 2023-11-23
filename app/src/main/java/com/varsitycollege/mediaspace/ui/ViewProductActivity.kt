package com.varsitycollege.mediaspace.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.ColourAdapter
import com.varsitycollege.mediaspace.data.SizeAdapter

class ViewProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)

        val gridViewColors: GridView = findViewById(R.id.gridViewColors)

        // Dummy color data
        val dummyColors = listOf("#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#008080", "#800080")

        val colorAdapter = ColourAdapter(this, dummyColors)
        gridViewColors.adapter = colorAdapter

        val gridViewSizes: GridView = findViewById(R.id.gridViewSizes)
        val dummySizes = listOf("XS", "S", "M", "L", "XL", "XXL")

        val sizeAdapter = SizeAdapter(this, dummySizes)
        gridViewSizes.adapter = sizeAdapter
        sizeAdapter.notifyDataSetChanged()
    }
}