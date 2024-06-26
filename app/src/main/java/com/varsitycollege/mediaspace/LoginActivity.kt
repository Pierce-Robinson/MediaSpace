package com.varsitycollege.mediaspace

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.varsitycollege.mediaspace.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        //If user is currently logged in, go to home page immediately
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
           redirectToHome()
        }

        //Login to home
        binding.loginButton.setOnClickListener{
            login()
        }

        //redirect to sign up
        binding.signUpTextTextView.setOnClickListener{
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }
    }
    private fun redirectToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onResume() {
        super.onResume()
        //Reset errors when view configuration changes
        binding.emailEditTextField.error = null
        binding.loginPasswordTextField.error = null
    }

    private fun login() {
        try {
            //Reset errors

            //Get attribute colors
            //https://stackoverflow.com/questions/73722004/how-to-get-color-which-is-defined-as-attribute-for-different-themes-in-android
            //user answered
            //https://stackoverflow.com/users/534471/emanuel-moecklin
            //accessed 25 October 2023
            binding.emailEditTextField.error = null
            var color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface, Color.GRAY)
            binding.emailEditTextField.boxBackgroundColor = color
            binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.GRAY)))

            binding.loginPasswordTextField.error = null
            binding.loginPasswordTextField.boxBackgroundColor = color
            binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.GRAY)))
            binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.GRAY)))

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            //Validate
            var hasError = false
            color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorErrorContainer, Color.GRAY)
            if (email.isBlank()) {
                binding.emailEditTextField.error = "Required."
                binding.emailEditTextField.boxBackgroundColor = color
                binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                hasError = true
            }
            if (password.isBlank()) {
                binding.loginPasswordTextField.error = "Required."
                binding.loginPasswordTextField.boxBackgroundColor = color
                binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                hasError = true
            }

            if (!hasError){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener { exception ->
                    //Handle exception and provide feedback to user
                    if(exception.localizedMessage?.contains("badly formatted") == true) {
                        binding.emailEditTextField.error = "Email is incorrectly formatted."
                        binding.emailEditTextField.boxBackgroundColor = color
                        binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                    } else if (exception.localizedMessage?.contains("INVALID_LOGIN") == true) {
                        binding.emailEditTextField.error = "Invalid login details."
                        binding.loginPasswordTextField.error = "Invalid login details."
                        binding.emailEditTextField.boxBackgroundColor = color
                        binding.loginPasswordTextField.boxBackgroundColor = color
                        binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                    } else if (exception.localizedMessage?.contains("unusual activity") == true) {
                        binding.emailEditTextField.error = "Too many incorrect attempts, please try again later."
                        binding.loginPasswordTextField.error = "Too many incorrect attempts, please try again later."
                        binding.emailEditTextField.boxBackgroundColor = color
                        binding.loginPasswordTextField.boxBackgroundColor = color
                        binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                    }
                    else {
                        Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }
}