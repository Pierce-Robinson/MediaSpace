package com.varsitycollege.mediaspace

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.varsitycollege.mediaspace.data.User
import com.varsitycollege.mediaspace.databinding.ActivityUpdateProfileBinding
import com.varsitycollege.mediaspace.ui.ProfileFragment

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

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
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = database.getReference("users").child(userId)

            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(User::class.java)


                    binding.editTextTitle.setText(userData?.title.orEmpty())
                    binding.editTextFirstName.setText(userData?.firstName.orEmpty())
                    binding.editTextLastName.setText(userData?.lastName.orEmpty())
                    binding.editTextMobile.setText(userData?.mobile.orEmpty())

                    val notificationsEnabled = userData?.notifications ?: false
                    binding.notificationsSwitch.isChecked = notificationsEnabled
                    // TODO: daniel please do the rest for address informatiosn
                }
            }.addOnFailureListener { exception ->
                showToast("Failed to fetch the users data")
            }
        }


        binding.updateProfileButton.setOnClickListener {
            updateProfile()
        }

        // Handle the notifications switch
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateNotificationsSetting(isChecked)
        }

        // go back to previous view
//        binding.goBackTextView.setOnClickListener {
//            val profileFragment = ProfileFragment()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.scrollView, profileFragment)
//                .commit()
//        } //TODO: this doesnt work

    }

    // link: https://copyprogramming.com/howto/get-data-from-firebase-kotlin-code-example
    // date accesed: 21 November 2023
    // Author: CopyProgramming, Hilda Ramirez

    private fun updateProfile() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = database.getReference("users").child(userId)

            // map with updatd user data
            val updatedData = mapOf(
                "title" to binding.editTextTitle.text.toString(),
                "firstName" to binding.editTextFirstName.text.toString(),
                "lastName" to binding.editTextLastName.text.toString(),
                "mobile" to binding.editTextMobile.text.toString()
                // TODO: daniel please do the rest for address informatiosn
            )

            // uppdate user info in firebase
            userRef.updateChildren(updatedData)
                .addOnSuccessListener {
                    showToast("Profile updated successfully")
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to update profile: ${exception.message}")
                }
        }
    }

    // Link: https://stackoverflow.com/questions/70692227/how-to-update-boolean-value-in-firestore-from-a-switch-tile
    // Author: Hrvoje Cukman
    // Date accessed: 21 November
    private fun updateNotificationsSetting(isChecked: Boolean) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = database.getReference("users").child(userId)
            userRef.child("notifications").setValue(isChecked)
                .addOnSuccessListener {
                    if (isChecked) {
                        showToast("You'll get notified now!")
                    } else {
                        showToast("Incognito, we like that, no more notifications for now!")
                    }
                }
                .addOnFailureListener { exception ->
                    showToast("Failed to update notifications setting: ${exception.message}")
                }
        }
    }


    // link: https://developer.android.com/guide/topics/ui/notifiers/toasts
    // Date accessed: 21 November 2023
    // AUhtor: Android Developers
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
