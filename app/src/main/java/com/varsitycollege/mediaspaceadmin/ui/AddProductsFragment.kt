package com.varsitycollege.mediaspaceadmin.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.varsitycollege.mediaspaceadmin.LoginActivity
import com.varsitycollege.mediaspaceadmin.data.CategoryAdapter
import com.varsitycollege.mediaspaceadmin.data.Colour
import com.varsitycollege.mediaspaceadmin.data.ColourAdapter
import com.varsitycollege.mediaspaceadmin.data.ImagesAdapter
import com.varsitycollege.mediaspaceadmin.data.Product
import com.varsitycollege.mediaspaceadmin.data.Size
import com.varsitycollege.mediaspaceadmin.databinding.FragmentAddProductsBinding

class AddProductsFragment : Fragment() {

    private var _binding: FragmentAddProductsBinding? = null
    private var colourHex = ""
    private var product = Product()
    private lateinit var  auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var imagesList: ArrayList<Uri> = arrayListOf()
    private var downloadUrls: ArrayList<String> = arrayListOf()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductsBinding.inflate(inflater, container, false)

        binding.colorRecycler.layoutManager = CarouselLayoutManager()
        binding.imageRecycler.layoutManager = CarouselLayoutManager()
        binding.categoryRecycler.layoutManager = CarouselLayoutManager()
//Start creation of product
        product = Product(sizeList = arrayListOf(
            Size("S", true),
            Size("M", true),
            Size("L", true),
            Size("XL", true),
            Size("2XL", true),
            Size("3XL", true),
            Size("4XL", true),
            Size("5XL", true),
        ))
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
                            binding.imageRecycler.visibility = GONE
                        }
                    }
                }
                adapter.registerAdapterDataObserver(observer)

                if (imagesList.isEmpty()) {
                    binding.imageRecycler.visibility = GONE
                }
                else {
                    binding.imageRecycler.visibility = VISIBLE
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
                        binding.colorRecycler.visibility = GONE
                    }
                }
            }
            adapter.registerAdapterDataObserver(observer)

            binding.colorRecycler.visibility = VISIBLE
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
                        binding.categoryRecycler.visibility = GONE
                    }
                }
            }
            adapter.registerAdapterDataObserver(observer)

            binding.categoryRecycler.visibility = VISIBLE
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
                    binding.categoryButton.visibility = GONE
                }
                else {
                    binding.categoryButton.visibility = VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //
            }

        })

        //Add product to database
        binding.addProductButton.setOnClickListener {
            auth = Firebase.auth
            if (auth.currentUser?.email != "admin@mediaspace.com") {
                //If this code runs there is a security breach
                Log.e("WARNING", "Attempted product add from unauthorized account")
                auth.signOut()
                //Go to login directly
                activity?.let {
                    val intent = Intent(it, LoginActivity::class.java)
                    it.startActivity(intent)
                    it.finish() // Finish the current activity after logout
                }
            }
            else if (validate()) {


                //get data for product
                //todo: disabling doesn't work properly
                binding.addProductButton.isEnabled = false
                product.sku = binding.skuEditText.text.toString()
                product.name = binding.nameEditText.text.toString()
                product.description = binding.descriptionEditText.text.toString()
                product.price = binding.priceEditText.text.toString().toDouble()
                product.stock = binding.stockEditText.text.toString().toInt()

                database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
                val ref = database.getReference("products")
                val key = ref.push()
                key.setValue(product).addOnSuccessListener {
                    Toast.makeText(this@AddProductsFragment.context, "Success", Toast.LENGTH_SHORT).show()
                    //Add images to created product
                    uploadImages(imagesList, key.key!!)

                    //use binding to clear all the fields after a successful add product
                    binding.skuEditText.text?.clear()
                    binding.nameEditText.text?.clear()
                    binding.descriptionEditText.text?.clear()
                    binding.priceEditText.text?.clear()
                    binding.stockEditText.text?.clear()
                    binding.colourNameEditText.text?.clear()
                    binding.colourAbbreviationEditText.text?.clear()
                    binding.categoryButton.visibility = GONE
                    colourHex = ""
                    validateColour()
                    binding.colourEditText.text?.clear()
                    binding.categoryEditText.text?.clear()

                    //clear data lists
                    imagesList.clear()
                    downloadUrls.clear()

                    //clear recyclerview adapters and update visibility
                    binding.imageRecycler.adapter = null
                    binding.imageRecycler.visibility = GONE
                    binding.colorRecycler.adapter = null
                    binding.colorRecycler.visibility = GONE
                    binding.categoryRecycler.adapter = null
                    binding.categoryRecycler.visibility = GONE
//Start creation of product
                    product = Product(sizeList = arrayListOf(
                        Size("S", true),
                        Size("M", true),
                        Size("L", true),
                        Size("XL", true),
                        Size("2XL", true),
                        Size("3XL", true),
                        Size("4XL", true),
                        Size("5XL", true),
                    ))
                }.addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this@AddProductsFragment.context, "Failed to add product: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.e("Failure", "Failed to add product", exception)
                }
                binding.addProductButton.isEnabled = true
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun validateColour() {
        var colour = MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorSurface, Color.GRAY)
        binding.colourCard.backgroundTintList = ColorStateList.valueOf(colour)
        try {
            colour = Color.parseColor("#${colourHex}")
            //Log.i("Get colour success","$colour")
            binding.colourCard.backgroundTintList = ColorStateList.valueOf(colour)
            binding.colourButton.visibility = VISIBLE
        } catch (e: Exception) {
            //Log.e("Get colour exception", ""+e.localizedMessage)
            binding.colourButton.visibility = GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validate(): Boolean {
        var valid = true

        if (binding.skuEditText.text.toString().isBlank()) {
            Toast.makeText(this@AddProductsFragment.context, "SKU missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.nameEditText.text.toString().isBlank()) {
            Toast.makeText(this@AddProductsFragment.context, "Name missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.descriptionEditText.text.toString().isBlank()) {
            Toast.makeText(this@AddProductsFragment.context, "Description missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.priceEditText.text.toString().isBlank()) {
            Toast.makeText(this@AddProductsFragment.context, "Price missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.stockEditText.text.toString().isBlank()) {
            Toast.makeText(this@AddProductsFragment.context, "Stock missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.colourList == null) {
            Toast.makeText(this@AddProductsFragment.context, "Colours missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.colourList?.isEmpty() == true) {
            Toast.makeText(this@AddProductsFragment.context, "Colours missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.categoriesList == null) {
            Toast.makeText(this@AddProductsFragment.context, "Categories missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (product.categoriesList?.isEmpty() == true) {
            Toast.makeText(this@AddProductsFragment.context, "Categories missing", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    private fun uploadImages(images: ArrayList<Uri>, key: String) {
        for (i in images) {
            // Generate a file name based on current time in milliseconds
            val fileName = "product_${System.currentTimeMillis()}"
            // Get a reference to the Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference.child("product_images/")
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
                            val ref = database.getReference("products").child(key).child("imagesList")
                            ref.setValue(downloadUrls).addOnSuccessListener {
                                //Toast.makeText(this@AddProductsFragment.context, "Images added", Toast.LENGTH_LONG).show()
                                Log.i("Success", "Images added")
                            }.addOnFailureListener {
                                //Toast.makeText(this@AddProductsFragment.context, "Failed to add images", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@AddProductsFragment.context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

}