package com.varsitycollege.mediaspace.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspace.data.Colour
import com.varsitycollege.mediaspace.data.ColourAdapter
import com.varsitycollege.mediaspace.data.CustomProduct
import com.varsitycollege.mediaspace.data.Delivery
import com.varsitycollege.mediaspace.data.ImagePagerAdapter
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.Size
import com.varsitycollege.mediaspace.data.SizeAdapter
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.ActivityViewProductBinding
import org.checkerframework.common.returnsreceiver.qual.This

class ViewProductActivity : AppCompatActivity(), ColourAdapter.ColourSelectionCallback, SizeAdapter.SizeSelectionCallback {
    private lateinit var binding: ActivityViewProductBinding
    private var product = Product()
    private var quantity = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private var downloadUrls = arrayListOf<String>()
    private var downloadUris: ArrayList<Uri> = arrayListOf()
    private var selectSize: String? = null
    private var cartArray = arrayListOf<CustomProduct>()
    private var selectColour = Colour()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.colourGrid
        binding.sizeGrid

        database = FirebaseDatabase.getInstance()
        ref = database.getReference("customProduct")
        // registers a photo picker activity launcher in single select mode.
        // Link: https://developer.android.com/training/data-storage/shared/photopicker
        // accessed: 18 November 2023
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                //Clear any previous images from list
                downloadUris = arrayListOf()
                for (i in uris) {
                    downloadUris.add(i)
                }
                binding.openGalleryButton.setImageURI(downloadUris[0])

                //TODO move this to a better upload location
            }


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
        val sizeAdapter = SizeAdapter(sizeList, this)
        binding.sizeGrid.adapter = sizeAdapter

        val colourList = product.colourList ?: emptyList()
        val colourAdapter = ColourAdapter(colourList, this)
        binding.colourGrid.adapter = colourAdapter

        binding.openGalleryButton.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

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
        binding.addToCart.setOnClickListener {
            if (downloadUris != null){
                val key = FirebaseAuth.getInstance().currentUser!!.uid
                uploadImages(downloadUris, key)
            }
            else {
                addToCart()
            }



        }
    }
    private fun addToCart() {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val sku = product.sku
        val prodName = product.name
        val price = product.price
        val quantity = binding.qtyEditText.text.toString().toInt()
        val userInstructions =
            binding.userInstructionsEditText.text.toString()
        val selectedColour = selectColour
        val selectedSize = selectSize
        val designUrl = downloadUrls


        val customProduct = CustomProduct(
            userId,
            sku,
            prodName,
            price,
            quantity,
            userInstructions,
            selectedColour,
            selectedSize,
            designUrl,
        )

        // Push the customProduct to the "cart" node in the Firebase Database
        val cartItemRef = database.getReference("users").child(userId).child("cart")
        val userRef = database.getReference("users").child(userId!!)
        userRef.get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            if (user != null) {
                //Get any existing deliveries
                if (user.cart != null) {
                    for (d in user.cart!!) {
                        cartArray.add(d)
                    }
                }
            }
        }
        cartArray.add(customProduct)
    cartItemRef.setValue(cartArray)
    }
    private fun uploadImages(images: ArrayList<Uri>, key: String) {
        for (i in images) {
            // Generate a file name based on current time in milliseconds
            val fileName = "design_${System.currentTimeMillis()}"
            // Get a reference to the Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference.child("user_design_images/")
            // Create a reference to the file location in Firebase Storage
            val imageRef = storageRef.child(fileName)

            val uploadTask = imageRef.putFile(i)
            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Image upload successful
                    imageRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uri = task.result
                            downloadUrls.add(uri.toString())
                            downloadUrls.add(product.imagesList?.firstOrNull().toString())
                            addToCart()
                            //Add image urls to submitted product

                            val ref = database.getReference("products").child(key).child("imagesList")
                            ref.setValue(downloadUrls).addOnSuccessListener {
                                Log.i("Success", "Images added")
                            }.addOnFailureListener {
                                Log.i("Failure", "Failed to add images")
                            }

                        } else {
                            // Image upload failed
                            Log.e("Image upload error","Failed to upload image")
                        }
                    }
                }
            }
            uploadTask.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onColourSelected(colour: Colour) {
        // Update your TextView with the selected colour name
        val textView = binding.txtColour
        textView.text = "Selected Colour: ${colour.name}"
        selectColour = colour
    }
    override fun onSizeSelected(size: String) {
        val textView = binding.txtSizes
        textView.text = "Selected Size: ${size}"
        selectSize = size
    }

}
