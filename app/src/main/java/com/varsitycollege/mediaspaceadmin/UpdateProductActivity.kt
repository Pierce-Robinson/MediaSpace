package com.varsitycollege.mediaspaceadmin

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspaceadmin.data.CategoryAdapter
import com.varsitycollege.mediaspaceadmin.data.Colour
import com.varsitycollege.mediaspaceadmin.data.ColourAdapter
import com.varsitycollege.mediaspaceadmin.data.ImagesAdapter
import com.varsitycollege.mediaspaceadmin.data.Product
import com.varsitycollege.mediaspaceadmin.data.Size
import com.varsitycollege.mediaspaceadmin.databinding.ActivityHomeBinding
import com.varsitycollege.mediaspaceadmin.databinding.ActivityUpdateProductBinding

class UpdateProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProductBinding

    private var imagesList: ArrayList<Uri> = arrayListOf()
    private var downloadUrls: ArrayList<String> = arrayListOf()
    private var colourHex = ""
    private var product = Product()
    private lateinit var database: FirebaseDatabase
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Start creation of product
        product = Product(
            sku = intent.getStringExtra("sku"),
            name = intent.getStringExtra("name"),
            description = intent.getStringExtra("description"),
            price = intent.getDoubleExtra("price", 0.0),
            stock = intent.getIntExtra("stock", 0),
            colourList = intent.getSerializableExtra("colours") as ArrayList<Colour>?,
            categoriesList = intent.getSerializableExtra("categories") as ArrayList<String>?,
            imagesList = arrayListOf(),
            sizeList = arrayListOf(
                Size("S", true),
                Size("M", true),
                Size("L", true),
                Size("XL", true),
                Size("2XL", true),
                Size("3XL", true),
                Size("4XL", true),
                Size("5XL", true),
            ))

        binding.updateProductHeader.text = "Update product: ${product.sku}"
        binding.nameEditText.setText(product.name)
        binding.descriptionEditText.setText(product.description)
        binding.priceEditText.setText("" + product.price)
        binding.stockEditText.setText("" + product.stock)

        binding.colorRecycler.layoutManager = CarouselLayoutManager()
        binding.categoryRecycler.layoutManager = CarouselLayoutManager()
        binding.imageRecycler.layoutManager = CarouselLayoutManager()

        val categoryAdapter = product.categoriesList?.let { CategoryAdapter(it) }
        binding.categoryRecycler.adapter = categoryAdapter
        binding.categoryRecycler.visibility = VISIBLE

        val colourAdapter = product.colourList?.let { ColourAdapter(it) }
        binding.colorRecycler.adapter = colourAdapter
        binding.colorRecycler.visibility = VISIBLE

        //Colour add button
        binding.colourButton.setOnClickListener {
            if (product.colourList == null){
                product.colourList = arrayListOf()
            }

            product.colourList!!.add(Colour(colourHex, binding.colourNameEditText.text.toString(), binding.colourAbbreviationEditText.text.toString(), true))
            val adapter = ColourAdapter(product.colourList!!)

            val observer = object: RecyclerView.AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    //Toast.makeText(this@AddProductsFragment.context, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
                    if (adapter.itemCount == 0) {
                        binding.colorRecycler.visibility = View.GONE
                    }
                }
            }
            adapter.registerAdapterDataObserver(observer)

            binding.colorRecycler.visibility = View.VISIBLE
            binding.colorRecycler.adapter = adapter
        }

        //Category add button
        binding.categoryButton.setOnClickListener {
            if (product.categoriesList == null) {
                product.categoriesList = arrayListOf()
            }

            product.categoriesList!!.add(binding.categoryEditText.text.toString())
            val adapter = CategoryAdapter(product.categoriesList!!)

            //observer for when items are deleted from adapter
            //https://github.com/realm/realm-android-adapters/issues/122
            //accessed 20 November 2023
            val observer = object: RecyclerView.AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    //Toast.makeText(this@AddProductsFragment.context, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
                    if (adapter.itemCount == 0) {
                        binding.categoryRecycler.visibility = View.GONE
                    }
                }
            }
            adapter.registerAdapterDataObserver(observer)

            binding.categoryRecycler.visibility = View.VISIBLE
            binding.categoryRecycler.adapter = adapter
        }

        // registers a photo picker activity launcher in multi select mode.
        // Link: https://developer.android.com/training/data-storage/shared/photopicker
        // accessed: 21 November 2023
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
                //Clear any previous images from list
                imagesList = arrayListOf()
                for (i in uris) {
                    imagesList.add(i)
                }
                val adapter = ImagesAdapter(imagesList)

                //observer for when items are deleted from adapter
                //https://github.com/realm/realm-android-adapters/issues/122
                //accessed 20 November 2023
                val observer = object: RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        //Toast.makeText(this@AddProductsFragment.context, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
                        if (adapter.itemCount == 0) {
                            binding.imageRecycler.visibility = View.GONE
                        }
                    }
                }
                adapter.registerAdapterDataObserver(observer)

                if (imagesList.isEmpty()) {
                    binding.imageRecycler.visibility = View.GONE
                }
                else {
                    binding.imageRecycler.visibility = View.VISIBLE
                }
                binding.imageRecycler.adapter = adapter
            }

        binding.addImagesButton.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //Colour add button
        binding.colourButton.setOnClickListener {
            if (product.colourList == null){
                product.colourList = arrayListOf()
            }

            product.colourList!!.add(Colour(colourHex, binding.colourNameEditText.text.toString(), binding.colourAbbreviationEditText.text.toString(), true))
            val adapter = ColourAdapter(product.colourList!!)

            val observer = object: RecyclerView.AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    //Toast.makeText(this@AddProductsFragment.context, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
                    if (adapter.itemCount == 0) {
                        binding.colorRecycler.visibility = View.GONE
                    }
                }
            }
            adapter.registerAdapterDataObserver(observer)

            binding.colorRecycler.visibility = View.VISIBLE
            binding.colorRecycler.adapter = adapter
        }

        //Category add button
        binding.categoryButton.setOnClickListener {
            if (product.categoriesList == null) {
                product.categoriesList = arrayListOf()
            }

            product.categoriesList!!.add(binding.categoryEditText.text.toString())
            val adapter = CategoryAdapter(product.categoriesList!!)

            //observer for when items are deleted from adapter
            //https://github.com/realm/realm-android-adapters/issues/122
            //accessed 20 November 2023
            val observer = object: RecyclerView.AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    //Toast.makeText(this@AddProductsFragment.context, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
                    if (adapter.itemCount == 0) {
                        binding.categoryRecycler.visibility = View.GONE
                    }
                }
            }
            adapter.registerAdapterDataObserver(observer)

            binding.categoryRecycler.visibility = View.VISIBLE
            binding.categoryRecycler.adapter = adapter
        }

        //colour text changed listeners
        //https://www.tutorialkart.com/kotlin-android/android-edittext-on-text-change/#gsc.tab=0
        //accessed 20 November 2023
        binding.colourEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                colourHex = s.toString()
                if (binding.colourEditText.text.toString().isNotBlank() && binding.colourNameEditText.text.toString().isNotBlank() && binding.colourAbbreviationEditText.text.toString().isNotBlank()) {
                    validateColour()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

        binding.colourNameEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.colourEditText.text.toString().isNotBlank() && binding.colourNameEditText.text.toString().isNotBlank() && binding.colourAbbreviationEditText.text.toString().isNotBlank()) {
                    validateColour()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

        binding.colourAbbreviationEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.colourEditText.text.toString().isNotBlank() && binding.colourNameEditText.text.toString().isNotBlank() && binding.colourAbbreviationEditText.text.toString().isNotBlank()) {
                    validateColour()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

        //category text changed listener
        binding.categoryEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.toString().isBlank()) {
                    binding.categoryButton.visibility = View.GONE
                }
                else {
                    binding.categoryButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

        //Update product
        binding.updateProductButton.setOnClickListener {
            auth = Firebase.auth
            if (auth.currentUser?.email != "admin@mediaspace.com") {
                //If this code runs there is a security breach
                Log.e("WARNING", "Attempted product update from unauthorized account")
                auth.signOut()
                //Go to login directly
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finish the current activity after logout
            }
            else if (validate()) {
                database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
                //update product details
                //todo: disabling doesn't work properly
                binding.updateProductButton.isEnabled = false

                val ref = database.getReference("products")
                ref.get().addOnSuccessListener {
                    for (p in it.children) {
                        val currentProduct = p.getValue(Product::class.java)
                        if (currentProduct != null) {
                            if (currentProduct.sku.equals(product.sku)) {
                                //if user added images, replace them
                                if (imagesList.isNotEmpty() && currentProduct.imagesList != null) {
                                    deleteImagesRecursive(currentProduct.imagesList!!, 0, imagesList, p.key!!)
                                }
                                else if (imagesList.isNotEmpty()) {
                                    uploadImages(imagesList, p.key!!)
                                }

                                //Update product
                                //Name
                                ref.child(p.key!!).child("name").setValue(binding.nameEditText.text.toString())
                                    .addOnSuccessListener {
                                        Log.i("Name updated", "Set to ${binding.nameEditText.text.toString()}")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Name update fail", "Failed to update name")
                                    }
                                //Description
                                ref.child(p.key!!).child("description").setValue(binding.descriptionEditText.text.toString())
                                    .addOnSuccessListener {
                                        Log.i("Description updated", "Set to ${binding.descriptionEditText.text.toString()}")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Description update fail", "Failed to update description")
                                    }
                                //Price
                                ref.child(p.key!!).child("price").setValue(binding.priceEditText.text.toString().toDouble())
                                    .addOnSuccessListener {
                                        Log.i("Price updated", "Set to ${binding.priceEditText.text.toString().toDouble()}")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Price update fail", "Failed to update price")
                                    }
                                //Stock
                                ref.child(p.key!!).child("stock").setValue(binding.stockEditText.text.toString().toInt())
                                    .addOnSuccessListener {
                                        Log.i("Stock updated", "Set to ${binding.stockEditText.text.toString().toInt()}")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Stock update fail", "Failed to update stock")
                                    }
                                //Colours
                                ref.child(p.key!!).child("colourList").setValue(product.colourList!!)
                                    .addOnSuccessListener {
                                        Log.i("Colours updated", "Set with ${product.colourList!!.size} values")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Colours update fail", "Failed to update colours")
                                    }
                                //Categories
                                ref.child(p.key!!).child("categoriesList").setValue(product.categoriesList!!)
                                    .addOnSuccessListener {
                                        Log.i("Categories updated", "Set with ${product.categoriesList!!.size} values")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Categories update fail", "Failed to update Categories")
                                    }

                                if (imagesList.isEmpty()) {
                                    //Display result
                                    Toast.makeText(applicationContext, "Updated ${product.sku}", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(applicationContext, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            }
                        }
                    }
                }

                binding.updateProductButton.isEnabled = true
            }
        }

    }

    private fun deleteImagesRecursive(imagesList: List<String>, index: Int, newImages: ArrayList<Uri>, key: String) {
        if (index < imagesList.size) {
            try {
                val test = FirebaseStorage.getInstance().getReferenceFromUrl(imagesList[index])
                test.delete()
                    .addOnSuccessListener {
                        Log.i("Image deleted", "Successfully deleted ${test.name}")
                        deleteImagesRecursive(imagesList, index + 1, newImages, key) // Move to the next image
                    }
                    .addOnFailureListener {
                        Log.e("Image delete fail", "Failed to delete ${test.name}")
                        // Handle failure if needed
                    }
            } catch (e: Exception) {
                Log.e("Image Delete Exception", "${e.message}")
            }
        } else {
            // All images are deleted, upload new images
            uploadImages(newImages, key)
        }
    }

    private fun validateColour() {
        var colour = MaterialColors.getColor(applicationContext, com.google.android.material.R.attr.colorSurface, Color.GRAY)
        binding.colourCard.backgroundTintList = ColorStateList.valueOf(colour)
        try {
            colour = Color.parseColor("#${colourHex}")
            //Log.i("Get colour success","$colour")
            binding.colourCard.backgroundTintList = ColorStateList.valueOf(colour)
            binding.colourButton.visibility = View.VISIBLE
        } catch (e: Exception) {
            //Log.e("Get colour exception", ""+e.localizedMessage)
            binding.colourButton.visibility = View.GONE
        }
    }

    private fun validate(): Boolean {
        var valid = true

        if (binding.nameEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Name missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.descriptionEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Description missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.priceEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Price missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.stockEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Stock missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.colourList == null) {
            Toast.makeText(applicationContext, "Colours missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.colourList?.isEmpty() == true) {
            Toast.makeText(applicationContext, "Colours missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.categoriesList == null) {
            Toast.makeText(applicationContext, "Categories missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.categoriesList?.isEmpty() == true) {
            Toast.makeText(applicationContext, "Categories missing", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    private fun uploadImages(images: ArrayList<Uri>, key: String, index: Int = 0) {
        if (index < images.size) {
            val fileName = "product_${System.currentTimeMillis()}"
            val storageRef = FirebaseStorage.getInstance().reference.child("product_images/")
            val imageRef = storageRef.child(fileName)

            val uploadTask = imageRef.putFile(images[index])
            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    imageRef.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uri = task.result
                            downloadUrls.add(uri.toString())

                            val ref = database.getReference("products").child(key).child("imagesList")
                            ref.setValue(downloadUrls).addOnSuccessListener {
                                Log.i("Success", "Images added")
                            }.addOnFailureListener {
                                Log.i("Failure", "Failed to add images")
                            }

                            // Move to the next image
                            uploadImages(images, key, index + 1)
                        } else {
                            Log.e("Image upload error", "Failed to get download URL")
                        }
                    }
                }
            }
            uploadTask.addOnFailureListener {
                Log.e("Image upload error", "Failed to upload image")
            }
        }
        else {
            //All images added, finish
            Toast.makeText(applicationContext, "Updated ${product.sku}", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}