package com.varsitycollege.mediaspace.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varsitycollege.mediaspace.LoginActivity
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance("https://mediaspace-d13bb-default-rtdb.europe-west1.firebasedatabase.app/")
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        getUserData()

        //Handle sign out
        binding.logoutButton.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val currentUser: FirebaseUser? = auth.currentUser
            if (currentUser != null) {
                logOut()
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logOut() {
        auth.signOut()
        activity?.let {
            val intent = Intent(it, LoginActivity::class.java)
            it.startActivity(intent)
            it.finish() // Finish the current activity after logout
        }
    }

    private fun getUserData() {
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid

    }

}