package com.varsitycollege.mediaspaceadmin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspaceadmin.BuildConfig
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.FirebaseDatabase

import com.varsitycollege.mediaspaceadmin.LoginActivity
import com.varsitycollege.mediaspaceadmin.data.User
import com.varsitycollege.mediaspaceadmin.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

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
                    it.finish() // Finish the current activity after logout
                }
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun logOut() {
        auth.signOut()
        activity?.let {
            val intent = Intent(it, LoginActivity::class.java)
            it.startActivity(intent)
            it.finish() // Finish the current activity after logout
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //firebase initialse here
        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            //check if user is authenticayed
            databaseReference = FirebaseDatabase.getInstance().reference
            fetchUserData()
            fetchProductData()
            fetchOrderData()
        } else {
            //TODO: not a real user. then voettsek
            logOut()
        }
    }

    private fun fetchUserData() {
        databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCount = snapshot.childrenCount.toInt()
                if (_binding != null) {
                    binding.totalUsers.text = "$userCount"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Failed to fetch user data: ${error.message}")
            }
        })
    }

    private fun fetchProductData() {
        databaseReference.child("products").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productCount = snapshot.childrenCount.toInt()
                if (_binding != null) {
                    binding.totalProducts.text = "$productCount"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Failed to fetch product data: ${error.message}")
            }
        })
    }

    private fun fetchOrderData() {
        databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // gets the total orders
               var users = ArrayList<User>()
                for (u in snapshot.children){
                    val user = u.getValue(User::class.java)
                    if (user != null){
                        users.add(user)
                    }
                }
                var orderCount = 0
                var pendingCount = 0
                for (user in users){
                    if (user.orderHistory != null) {
                        for (order in user.orderHistory!!) {
                            orderCount++
                            // get the total pending orders
                            if (order.status=="Pending"){
                                pendingCount++
                            }
                        }
                    }
                }
                if (_binding != null) {
                    binding.totalOrders.text = "$orderCount"
                    binding.pendingOrders.text = "$pendingCount"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardFragment", "Failed to fetch order data: ${error.message}")
            }
        })
    }

}