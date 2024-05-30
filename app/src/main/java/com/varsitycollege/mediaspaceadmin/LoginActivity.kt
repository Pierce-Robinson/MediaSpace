package com.varsitycollege.mediaspaceadmin

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.varsitycollege.mediaspaceadmin.databinding.ActivityLoginBinding
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val authenticator = BiometricManager.Authenticators.BIOMETRIC_STRONG
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check if biometric authentication is available
        checkAuth()

        //Set up biometric prompt
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.i("Authentication status", "Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.i("Authentication status", "Authentication succeeded!")

                    //Go to home page on biometric auth success
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.i("Authentication status", "Authentication failed")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Verify it's you")
            .setSubtitle("For your security, please scan your fingerprint.")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(authenticator)
            .build()

        //Login button handler
        binding.loginButton.setOnClickListener {
            login()
        }

        //If admin has previously logged in, the must still authenticate with their biometrics
        //https://developer.android.com/training/sign-in/biometric-auth
        //Accessed 17 November 2023
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            //Ask for fingerprint
            biometricPrompt.authenticate(promptInfo)
        }

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
            binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(
                MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.GRAY)))
            binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.GRAY)))

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            //Validate
            var hasError = false
            color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorErrorContainer, Color.GRAY)
            if (email.isBlank()) {
                binding.emailEditTextField.error = "Required."
                binding.emailEditTextField.boxBackgroundColor = color
                binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(
                    MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                hasError = true
            }
            if (password.isBlank()) {
                binding.loginPasswordTextField.error = "Required."
                binding.loginPasswordTextField.boxBackgroundColor = color
                binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(
                    MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(
                    MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                hasError = true
            }

            if (!hasError){
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        //Only allow admin account to authenticate
                        if (auth.currentUser?.email == "admin@mediaspace.com") {
                            //Ask for fingerprint
                            biometricPrompt.authenticate(promptInfo)
                        }
                        else {
                            //Show invalid error
                            binding.emailEditTextField.error = "Invalid login details."
                            binding.loginPasswordTextField.error = "Invalid login details."
                            binding.emailEditTextField.boxBackgroundColor = color
                            binding.loginPasswordTextField.boxBackgroundColor = color
                            binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(
                                MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                            binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(
                                MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                            binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(
                                MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))

                            //Clear auth
                            auth.signOut()
                        }
                    }
                }.addOnFailureListener { exception ->
                    //Handle exception and provide feedback to user
                    if(exception.localizedMessage?.contains("badly formatted") == true) {
                        binding.emailEditTextField.error = "Email is incorrectly formatted."
                        binding.emailEditTextField.boxBackgroundColor = color
                        binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                    } else if (exception.localizedMessage?.contains("INVALID_LOGIN") == true) {
                        binding.emailEditTextField.error = "Invalid login details."
                        binding.loginPasswordTextField.error = "Invalid login details."
                        binding.emailEditTextField.boxBackgroundColor = color
                        binding.loginPasswordTextField.boxBackgroundColor = color
                        binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                    } else if (exception.localizedMessage?.contains("unusual activity") == true) {
                        binding.emailEditTextField.error = "Too many incorrect attempts, please try again later."
                        binding.loginPasswordTextField.error = "Too many incorrect attempts, please try again later."
                        binding.emailEditTextField.boxBackgroundColor = color
                        binding.loginPasswordTextField.boxBackgroundColor = color
                        binding.emailEditTextField.setStartIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setStartIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
                        binding.loginPasswordTextField.setEndIconTintList(ColorStateList.valueOf(
                            MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.GRAY)))
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

    override fun onResume() {
        super.onResume()
        //Reset errors when view configuration changes
        binding.emailEditTextField.error = null
        binding.loginPasswordTextField.error = null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkAuth() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(authenticator)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d("Biometric Auth Result", "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("Biometric Auth Result", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("Biometric Auth Result", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                Log.d("Biometric Auth Result", "A security update is required.")
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                Log.d("Biometric Auth Result", "Biometrics are unsupported.")
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                Log.d("Biometric Auth Result", "Biometric status is unknown.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, authenticator)
                }
                //startActivityForResult(enrollIntent, 1)
                val startForEnroll: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        val data = result.data
                        Log.d("Activity Result Data", "$data")
                    }
                }
                startForEnroll.launch(enrollIntent)
            }
        }
    }

}