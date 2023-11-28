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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        //get the user data from firebase to populate it first into the textboxes
        val user = intent.getSerializableExtra("user") as User?
        if (user != null) {
            currentUser = user
            binding.editTextTitle.setText(currentUser.title.orEmpty())
            binding.editTextFirstName.setText(currentUser.firstName.orEmpty())
            binding.editTextLastName.setText(currentUser.lastName.orEmpty())
            binding.editTextMobile.setText(currentUser.mobile.orEmpty())

            val notificationsEnabled = currentUser.notifications ?: false
            binding.notificationsSwitch.isChecked = notificationsEnabled
        }
        else {
            currentUser = User()
        }

        binding.updateProfileButton.setOnClickListener {
            updateProfile()
        }

        binding.recyclerViewDelivery.layoutManager = LinearLayoutManager(applicationContext)
        //Add dummy address to current user to enable add button
        if (currentUser.deliveryAddresses == null) {
            currentUser.deliveryAddresses = arrayListOf()
            currentUser.deliveryAddresses!!.add(Delivery("add"))
        }
        else {
            refreshData()
        }

        val adapter = DeliveryAdapter(currentUser.deliveryAddresses!!)
        binding.recyclerViewDelivery.adapter = adapter

    }

    override fun onResume() {
        super.onResume()

        //Refresh data when coming back from changing delivery data
        refreshData()
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

        //update user info in firebase
        userRef.updateChildren(updatedData)
            .addOnSuccessListener {
                showToast("Profile updated successfully")
            }
            .addOnFailureListener { exception ->
                showToast("Failed to update profile: ${exception.message}")
            }
    }

    private fun refreshData() {
        val userId = auth.uid
        val userRef = database.getReference("users").child(userId!!).child("deliveryAddresses")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser.deliveryAddresses!!.clear()
                for (d in snapshot.children) {
                    val delivery = d.getValue(Delivery::class.java)
                    if (delivery != null) {
                        currentUser.deliveryAddresses!!.add(delivery)
                    }
                }
                currentUser.deliveryAddresses!!.add(Delivery("add"))
                val adapter = DeliveryAdapter(currentUser.deliveryAddresses!!)
                binding.recyclerViewDelivery.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("" + error)
            }

        })

    }

    // link: https://developer.android.com/guide/topics/ui/notifiers/toasts
    // Date accessed: 21 November 2023
    // Author: Android Developers
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
