package com.varsitycollege.mediaspace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.varsitycollege.mediaspace.data.Delivery
import com.varsitycollege.mediaspace.databinding.ActivityDeliveryBinding

class DeliveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val delivery = intent.getSerializableExtra("delivery") as Delivery?

        if (delivery != null) {
            binding.postalCodeEditText.setText(delivery.postalCode)
            binding.addressLineOneEditText.setText(delivery.addressLineOne)
            binding.addressLineTwoEditText.setText(delivery.addressLineTwo)
            binding.editTextSuburb.setText(delivery.suburb)
            binding.editTextCityTown.setText(delivery.city)
            binding.editTextCountry.setText(delivery.country)

            binding.submitAddressInfo.text = "Update"
        }
        else {
            binding.submitAddressInfo.text = "Add"
        }

    }

    // link: https://copyprogramming.com/howto/get-data-from-firebase-kotlin-code-example
    // date accesed: 21 November 2023
    // Author: CopyProgramming, Hilda Ramirez

    private fun updateProfile() {
        val userId = auth.uid
        val userRef = database.getReference("users").child(userId!!)

        // map with updated user data
        val updatedData = mapOf(
            "customerId" to auth.uid,
            "postalCode" to binding.postalCodeEditText.text.toString(),
            "addressLineOne" to binding.addressLineOneEditText.text.toString(),
            "addressLineTwo" to binding.addressLineTwoEditText.text.toString(),
            "suburb" to binding.editTextSuburb.text.toString(),
            "city" to binding.editTextCityTown.text.toString(),
            "country" to binding.editTextCountry.text.toString()
        )

        // update user info in firebase
        userRef.updateChildren(updatedData)
            .addOnSuccessListener {
                showToast("Profile updated successfully")
            }
            .addOnFailureListener { exception ->
                showToast("Failed to update profile: ${exception.message}")
            }
    }

    private fun validate() {

    }

    // link: https://developer.android.com/guide/topics/ui/notifiers/toasts
    // Date accessed: 21 November 2023
    // Author: Android Developers
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}