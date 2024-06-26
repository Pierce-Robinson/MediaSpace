package com.varsitycollege.mediaspace.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspace.BuildConfig
import com.varsitycollege.mediaspace.LoginActivity
import com.varsitycollege.mediaspace.OrderHistoryActivity
import com.varsitycollege.mediaspace.UpdateProfileActivity
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private var user = User()

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        database = FirebaseDatabase.getInstance(BuildConfig.rtdb_conn)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        getUserData()

        //Handle sign out
        binding.logoutButton.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                logOut()
            }
            else {
                //If this code runs there is a security breach
                Log.e("WARNING", "Attempted log out while not being logged in")
                //Go to login directly
                activity?.let {
                    val intent = Intent(it, LoginActivity::class.java)
                    it.startActivity(intent)
                    it.finish() //Finish the current activity after logout
                }
            }
        }

        //Handle delete account
        binding.deleteAccountButton.setOnClickListener {
            deleteAccount()
        }

        //button for personal info
        binding.btnPersonalInfo.setOnClickListener {
            navigateToUpdateProfile()
        }

        binding.btnOrderHistory.setOnClickListener{
            orderHistory()
        }

        return binding.root
    }

    private fun navigateToUpdateProfile() {
        //Go to update profile with user data
        val intent = Intent(activity, UpdateProfileActivity::class.java)
            .putExtra("user", user)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun orderHistory() {
        activity?.let {
            val intent = Intent(it, OrderHistoryActivity::class.java)
            it.startActivity(intent)
        }
    }
    private fun logOut() {
        auth.signOut()
        activity?.let {
            val intent = Intent(it, LoginActivity::class.java)
            it.startActivity(intent)
            it.finish() //Finish the current activity after logout
        }
    }

    private fun deleteAccount() {
        //this will show a confirmation popup for deletion
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account?")
            .setPositiveButton("Yes") { _, _ ->
                //delete the user account
                auth = FirebaseAuth.getInstance()
                val id = auth.currentUser?.uid
                if (id != null) {
                    //Todo: Delete user orders here
                    try {
                        //Delete user's object (Before account deletion to retain permissions
                        ref = database.getReference("users")
                        ref.child(id).removeValue().addOnSuccessListener {
                            //On object deletion success, delete user account and return to the login screen
                            auth.currentUser?.delete()?.addOnSuccessListener {
                                activity?.let {
                                    val intent = Intent(it, LoginActivity::class.java)
                                    it.startActivity(intent)
                                    it.finish() //Finish the current activity after logout
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                this@ProfileFragment.requireActivity().applicationContext,
                                it.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ProfileFragment.requireActivity().applicationContext,
                            "Please login, then try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        logOut()
                    }
                } else {
                    Toast.makeText(
                        this@ProfileFragment.requireActivity().applicationContext,
                        "Please login, then try again.",
                        Toast.LENGTH_LONG
                    ).show()
                    logOut()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun getUserData() {
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        if (id != null) {
            ref = database.getReference("users").child(id)

            // Add a ValueEventListener to get the maxDistance value
            // Link: https://stackoverflow.com/questions/42986449/firebase-value-event-listener
            // date: 17 October 2023
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        user = dataSnapshot.getValue(User::class.java)!!
                        user.orderHistory = arrayListOf()
                        user.cart = arrayListOf()
                        val displayName = user.title + " " + user.lastName

                        //Set the display name
                        if (_binding != null) {
                            binding.displayNameTextView.text = displayName
                            binding.btnPersonalInfo.isEnabled = true
                            binding.btnOrderHistory.isEnabled = true
                            binding.btnWishList.isEnabled = true
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Database error", "databaseError.message")
                }
            })
        }
    }

}