package com.example.app

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    // Declare EditText fields, button, and Firebase instances
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var createProfileButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize the views
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextAge = findViewById(R.id.editTextAge)
        editTextWeight = findViewById(R.id.editTextWeight)
        createProfileButton = findViewById(R.id.createProfileButton)

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("Users")

        // Handle the save profile button click
        createProfileButton.setOnClickListener {
            createProfile()
        }
    }

    private fun createProfile() {
        // Get user input from EditText fields
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()
        val age = editTextAge.text.toString().trim()
        val weight = editTextWeight.text.toString().trim()

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            editTextEmail.error = "Email cannot be empty"
            return
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.error = "Password cannot be empty"
            return
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.error = "Please confirm your password"
            return
        }

        if (password != confirmPassword) {
            editTextConfirmPassword.error = "Passwords do not match"
            return
        }

        if (TextUtils.isEmpty(age)) {
            editTextAge.error = "Age cannot be empty"
            return
        }

        if (!isNumeric(age)) {
            editTextAge.error = "Please enter a valid age"
            return
        }

        if (TextUtils.isEmpty(weight)) {
            editTextWeight.error = "Weight cannot be empty"
            return
        }

        if (!isNumeric(weight)) {
            editTextWeight.error = "Please enter a valid weight"
            return
        }

        // Register the user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save additional details to Firebase Database
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userMap = mapOf(
                        "email" to email,
                        "age" to age,
                        "weight" to weight
                    )
                    database.child(userId).setValue(userMap)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(this, "Profile Created Successfully", Toast.LENGTH_SHORT).show()
                                // Navigate to LoginActivity
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to save profile data", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Show warning on the same page
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Utility function to check if a string is a valid number
    private fun isNumeric(str: String): Boolean {
        return str.toDoubleOrNull() != null
    }
}
