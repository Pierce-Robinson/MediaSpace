package com.varsitycollege.mediaspace

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.varsitycollege.mediaspace.data.Delivery
import com.varsitycollege.mediaspace.data.DeliveryAdapter
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.ActivityUpdateProfileBinding
import com.varsitycollege.mediaspace.ui.ProfileFragment

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // link: https://firebase.google.com/docs/auth/android/password-auth
        // date accessed: 21 November 2023
        // author: Firebase
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)

        // get the user data from firebase to populate it first into the textboxwes
        currentUser = intent.getSerializableExtra("user") as User

        binding.editTextTitle.setText(currentUser.title.orEmpty())
        binding.editTextFirstName.setText(currentUser.firstName.orEmpty())
        binding.editTextLastName.setText(currentUser.lastName.orEmpty())
        binding.editTextMobile.setText(currentUser.mobile.orEmpty())

        val notificationsEnabled = currentUser.notifications ?: false
        binding.notificationsSwitch.isChecked = notificationsEnabled

        binding.updateProfileButton.setOnClickListener {
            updateProfile()
        }

        //TODO: DELIVERY ADDRESSES IN RECYCLER VIEW, SIMILAR TO MANAGE STOCK
        binding.recyclerViewDelivery.layoutManager = LinearLayoutManager(applicationContext)
        //Add dummy address to current user to enable add button
        if (currentUser.deliveryAddresses == null) {
            currentUser.deliveryAddresses = arrayListOf()
        }
        currentUser.deliveryAddresses!!.add(Delivery("add"))

        val adapter = DeliveryAdapter(currentUser.deliveryAddresses!!)
        binding.recyclerViewDelivery.adapter = adapter

    }

    // link: https://copyprogramming.com/howto/get-data-from-firebase-kotlin-code-example
    // date accesed: 21 November 2023
    // Author: CopyProgramming, Hilda Ramirez

    private fun updateProfile() {
        val userId = auth.uid
        val userRef = database.getReference("users").child(userId!!)

        // map with updated user data
        val updatedData = mapOf(
            "title" to binding.editTextTitle.text.toString(),
            "firstName" to binding.editTextFirstName.text.toString(),
            "lastName" to binding.editTextLastName.text.toString(),
            "mobile" to binding.editTextMobile.text.toString(),
            "notifications" to binding.notificationsSwitch.isChecked
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

    // link: https://developer.android.com/guide/topics/ui/notifiers/toasts
    // Date accessed: 21 November 2023
    // Author: Android Developers
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
