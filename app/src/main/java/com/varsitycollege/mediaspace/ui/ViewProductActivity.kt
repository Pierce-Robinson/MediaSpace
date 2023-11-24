package com.varsitycollege.mediaspace.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.GridView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.varsitycollege.mediaspace.data.Colour
import com.varsitycollege.mediaspace.data.ColourAdapter
import com.varsitycollege.mediaspace.data.ImagePagerAdapter
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.Size
import com.varsitycollege.mediaspace.data.SizeAdapter
import com.varsitycollege.mediaspace.databinding.ActivityViewProductBinding

class ViewProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewProductBinding
    private lateinit var gridViewColors: GridView
    private lateinit var gridViewSizes: GridView
    private lateinit var colorAdapter: ColourAdapter
    private lateinit var sizeAdapter: SizeAdapter
    private var product = Product()
    private var productList: ArrayList<Product> = arrayListOf()
    private var quantity = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.colourGrid
        binding.sizeGrid

        product = Product(
            sku = intent.getStringExtra("sku"),
            name = intent.getStringExtra("name"),
            description = intent.getStringExtra("description"),
            price = intent.getDoubleExtra("price", 0.0),
            stock = intent.getIntExtra("stock", 0),
            colourList = intent.getSerializableExtra("colours") as ArrayList<Colour>?,
            categoriesList = intent.getSerializableExtra("categories") as ArrayList<String>?,
            imagesList = intent.getSerializableExtra("images") as ArrayList<String>?,
            sizeList = intent.getSerializableExtra("sizes") as ArrayList<Size>
        )

        binding.prodSku.text = product.sku
        binding.prodName.text = product.name
        binding.txtDescription.text = "Description: \n${product.description}"
        binding.prodPrice.text = "R${product.price.toString()}"
        val concatenatedCategories = product.categoriesList?.joinToString(" / ")
        binding.txtCategories.text = "Categories: \n${concatenatedCategories}"

        for (i in product.imagesList!!) {
            val imageUrls = product.imagesList ?: emptyList()
            val imagePagerAdapter = ImagePagerAdapter(imageUrls)
            binding.productImage.adapter = imagePagerAdapter
        }

        for (i in product.imagesList!!) {
            val imageUrls = product.imagesList ?: emptyList()
            val imagePagerAdapter = ImagePagerAdapter(imageUrls)
            binding.productImage.adapter = imagePagerAdapter
        }
        val sizeList = product.sizeList ?: emptyList()
        val sizeAdapter = SizeAdapter(sizeList)
        binding.sizeGrid.adapter = sizeAdapter

        val colourList = product.colourList ?: emptyList()
        val colourAdapter = ColourAdapter(colourList)
        binding.colourGrid.adapter = colourAdapter


        binding.qtyEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the quantity when the text in the EditText changes
                if (!s.isNullOrBlank()) {
                    quantity = s.toString().toInt()
                } else {
                    quantity = 0 // Handle the case when the EditText is empty
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.addQuantity.setOnClickListener {
            // Increment the quantity
            quantity++
            // Update the EditText to display the new quantity
            binding.qtyEditText.setText(quantity.toString())
        }

        binding.removeQuantity.setOnClickListener {
            // Ensure the quantity is greater than 0 before decrementing
            if (quantity > 0) {
                // Decrement the quantity
                quantity--
                // Update the EditText to display the new quantity
                binding.qtyEditText.setText(quantity.toString())
            }
        }
    }

}
