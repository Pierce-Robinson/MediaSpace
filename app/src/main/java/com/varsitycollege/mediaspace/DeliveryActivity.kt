package com.varsitycollege.mediaspace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.ktx.Firebase
import com.varsitycollege.mediaspace.data.Delivery
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.ActivityDeliveryBinding

class DeliveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var delivery = Delivery()
    private var deliveryArray = arrayListOf<Delivery>()
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // link: https://firebase.google.com/docs/auth/android/password-auth
        // date accessed: 21 November 2023
        // author: Firebase
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)

        val specialDelivery = intent.getSerializableExtra("delivery") as Delivery?
        position = intent.getIntExtra("position", 0)

        if (specialDelivery != null) {
            delivery = specialDelivery

            binding.postalCodeEditText.setText(delivery.postalCode)
            binding.addressLineOneEditText.setText(delivery.addressLineOne)
            binding.addressLineTwoEditText.setText(delivery.addressLineTwo)
            binding.editTextSuburb.setText(delivery.suburb)
            binding.editTextCityTown.setText(delivery.city)
            binding.editTextCountry.setText(delivery.country)

            binding.submitAddressInfo.text = "Update"
            binding.submitAddressInfo.setOnClickListener {
                if (validate()) {
                    updateDelivery()
                }
            }
        }
        else {
            binding.submitAddressInfo.text = "Add"
            binding.submitAddressInfo.setOnClickListener {
                if (validate()) {
                    createDelivery()
                }
            }
        }

    }

    // link: https://copyprogramming.com/howto/get-data-from-firebase-kotlin-code-example
    // date accesed: 21 November 2023
    // Author: CopyProgramming, Hilda Ramirez

    private fun updateDelivery() {
        val userId = auth.uid
        val userRef = database.getReference("users").child(userId!!).child("deliveryAddresses").child(position.toString())

        //map with updated user data
        val updatedData = mapOf(
            "customerId" to auth.uid,
            "postalCode" to binding.postalCodeEditText.text.toString(),
            "addressLineOne" to binding.addressLineOneEditText.text.toString(),
            "addressLineTwo" to ""+binding.addressLineTwoEditText.text.toString(),
            "suburb" to binding.editTextSuburb.text.toString(),
            "city" to binding.editTextCityTown.text.toString(),
            "country" to binding.editTextCountry.text.toString()
        )

        //update user info in firebase
        userRef.updateChildren(updatedData)
            .addOnSuccessListener {
                showToast("Address updated successfully")
                val intent = Intent(applicationContext, UpdateProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                showToast("Failed to update address: ${exception.message}")
            }
    }

    private fun createDelivery() {
        val userId = auth.uid
        val userRef = database.getReference("users").child(userId!!)

        delivery.customerId = auth.uid
        delivery.postalCode = binding.postalCodeEditText.text.toString()
        delivery.addressLineOne = binding.addressLineOneEditText.text.toString()
        delivery.addressLineTwo = ""+binding.addressLineTwoEditText.text.toString()
        delivery.suburb = binding.editTextSuburb.text.toString()
        delivery.city = binding.editTextCityTown.text.toString()
        delivery.country = binding.editTextCountry.text.toString()

        //Get user data
        userRef.get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            if (user != null) {
                //Get any existing deliveries
                if (user.deliveryAddresses != null) {
                    for (d in user.deliveryAddresses!!) {
                        deliveryArray.add(d)
                    }
                }

                //Add new delivery
                deliveryArray.add(delivery)

                //Set deliveries and return to previous activity, passing along user data
                userRef.child("deliveryAddresses").setValue(deliveryArray)
                    .addOnSuccessListener {
                        showToast("Address created successfully")
                        val intent = Intent(applicationContext, UpdateProfileActivity::class.java)
                            .putExtra("user", user)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        showToast("Failed to update address: ${exception.message}")
                    }
            }
            else {
                showToast("Failed to find user.")
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun validate(): Boolean {
        var valid = true

        if (binding.postalCodeEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Postal code missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.addressLineOneEditText.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Address line missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.editTextSuburb.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Suburb missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.editTextCityTown.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "City missing", Toast.LENGTH_SHORT).show()
            valid = false
        }
        else if (binding.editTextCountry.text.toString().isBlank()) {
            Toast.makeText(applicationContext, "Country missing", Toast.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    // link: https://developer.android.com/guide/topics/ui/notifiers/toasts
    // Date accessed: 21 November 2023
    // Author: Android Developers
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}