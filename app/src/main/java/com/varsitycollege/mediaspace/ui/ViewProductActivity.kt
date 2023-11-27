package com.varsitycollege.mediaspace.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.HomeActivity
import com.varsitycollege.mediaspace.UpdateProfileActivity
import com.varsitycollege.mediaspace.data.Colour
import com.varsitycollege.mediaspace.data.ColourAdapter
import com.varsitycollege.mediaspace.data.CustomProduct
import com.varsitycollege.mediaspace.data.ImagePagerAdapter
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.Size
import com.varsitycollege.mediaspace.data.SizeAdapter
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.ActivityViewProductBinding

class ViewProductActivity : AppCompatActivity(), ColourAdapter.ColourSelectionCallback,
    SizeAdapter.SizeSelectionCallback {
    private lateinit var binding: ActivityViewProductBinding
    private var product = Product()
    private var quantity = 0
    private var index = 0
    private lateinit var database: FirebaseDatabase
    private var downloadUrls = arrayListOf<String>()
    private var downloadUris: ArrayList<Uri> = arrayListOf()
    private var selectedSize: String? = null
    private var selectedColour: Colour? = null
    private var customProduct = CustomProduct()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)

        // registers a photo picker activity launcher in single select mode.
        // Link: https://developer.android.com/training/data-storage/shared/photopicker
        // accessed: 18 November 2023
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                //Clear any previous images from list
                downloadUris.clear()
                downloadUrls.clear()
                for (i in uris) {
                    downloadUris.add(i)
                }
                if (downloadUris.isNotEmpty()) {
                    binding.openGalleryButton.setImageURI(downloadUris[0])
                } else {
                    binding.openGalleryButton.setImageURI(null)
                }
            }

        binding.openGalleryButton.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
        binding.txtDescription.text = Html.fromHtml("<b>Description:</b>  \n${product.description}", Html.FROM_HTML_MODE_LEGACY)
        binding.prodPrice.text = "R${product.price.toString()}"
        val concatenatedCategories = product.categoriesList?.joinToString(" / ")
        binding.txtCategories.text = Html.fromHtml("<b>Categories:</b> \n${concatenatedCategories}", Html.FROM_HTML_MODE_LEGACY)

        for (i in product.imagesList!!) {
            val imageUrls = product.imagesList ?: emptyList()
            val imagePagerAdapter = ImagePagerAdapter(imageUrls, arrayListOf(), 0)
            binding.productImage.adapter = imagePagerAdapter
        }

        for (i in product.imagesList!!) {
            val imageUrls = product.imagesList ?: emptyList()
            val imagePagerAdapter = ImagePagerAdapter(imageUrls, arrayListOf(),0)
            binding.productImage.adapter = imagePagerAdapter
        }
        val sizeList = product.sizeList ?: emptyList()
        val sizeAdapter = SizeAdapter(sizeList, this)
        binding.sizeGrid.adapter = sizeAdapter

        val colourList = product.colourList ?: emptyList()
        val colourAdapter = ColourAdapter(colourList, this)
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

        binding.addToCart.setOnClickListener {
            if (validate()) {
                addToCart()
            }
        }
    }


    // Link: https://copyprogramming.com/howto/kotlin-data-class-and-bean-validation-with-container-element-constraints
    // Author: James Bobbitt
    // Date accessed: 24 November 2023
    private fun validate(): Boolean {
        var valid = true

        if (selectedColour == null) {
            Toast.makeText(applicationContext, "Please select a colour", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (selectedSize == null) {
            Toast.makeText(applicationContext, "Please select a size", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (binding.qtyEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Please enter a quantity", Toast.LENGTH_SHORT).show()
            valid = false
        } else if (binding.qtyEditText.text.toString().toInt() <= 0) {
            Toast.makeText(applicationContext, "Please enter a valid quantity", Toast.LENGTH_SHORT)
                .show()
            valid = false
        }

        return valid
    }

    private fun addToCart() {

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val sku = product.sku
        val prodName = product.name
        val price = product.price
        val quantity = binding.qtyEditText.text.toString().toInt()
        var userInstructions = binding.userInstructionsEditText.text.toString()
        val selectedColour = selectedColour
        val selectedSize = selectedSize
        val designUrl = downloadUrls
        val firstImageUrl = product.imagesList?.firstOrNull()

        if (userInstructions == "") {
            userInstructions = "None"
        }

        customProduct = CustomProduct(
            userId,
            sku,
            prodName,
            price,
            quantity,
            userInstructions,
            selectedColour,
            selectedSize,
            designUrl,
            firstImageUrl,
        )

        // Push the customProduct to the "cart" node for the current user
        val userRef = database.getReference("users").child(userId)
        val cartArray: ArrayList<CustomProduct> = arrayListOf()

        //Get user data
        userRef.get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            if (user != null) {
                //Get any existing cart items
                if (user.cart != null) {
                    for (c in user.cart!!) {
                        cartArray.add(c)
                    }
                }

                //Add new cart item
                cartArray.add(customProduct)

                //update index
                index = cartArray.indexOf(customProduct)

                //Set cart
                userRef.child("cart").setValue(cartArray)
                    .addOnSuccessListener {
                        showToast("Cart updated successfully")
                        //upload images to the added cart item
                        if (downloadUris.size != 0) {
                            uploadImages(downloadUris, userId)
                        }
                        //TODO: maybe go back to trending page

                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    .addOnFailureListener { exception ->
                        showToast("Failed to update cart: ${exception.message}")
                    }
            } else {
                showToast("Failed to find user.")
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

    }

    // link: https://developer.android.com/guide/topics/ui/notifiers/toasts
    // Date accessed: 21 November 2023
    // Author: Android Developers
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

                            //Add image urls to submitted product
                            //TODO: make only upload once for final list

                            //add download urls to custom product
                            customProduct.design = downloadUrls

                            val ref = database.getReference("users").child(key).child("cart")
                                .child("" + index)
                            ref.setValue(customProduct).addOnSuccessListener {
                                //Toast.makeText(this@AddProductsFragment.context, "Images added", Toast.LENGTH_LONG).show()
                                Log.i("Success", "Images added")



                            }.addOnFailureListener {
                                //Toast.makeText(this@AddProductsFragment.context, "Failed to add images", Toast.LENGTH_LONG).show()
                                Log.i("Failure", "Failed to add images")
                            }

                        } else {
                            // Image upload failed
                            Log.e("Image upload error", "Failed to upload image")
                        }
                    }
                }
            }
            uploadTask.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
        //redirect after success add to cart, go to home activity
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onColourSelected(colour: Colour) {
        // Update your TextView with the selected colour name
        val textView = binding.txtColour
        textView.text = "Selected Colour: ${colour.name}"
        selectedColour = colour
    }

    override fun onSizeSelected(size: String) {
        val textView = binding.txtSizes
        textView.text = "Selected Size: ${size}"
        selectedSize = size
    }

}
