package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cardview)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        val bmi = findViewById<ImageView>(R.id.imgbmi)
        val sensor = findViewById<ImageView>(R.id.Imgpedo)
        val stepCount = findViewById<ImageView>(R.id.imageView3)
        val heartRate = findViewById<ImageView>(R.id.imageView4)
        val profile = findViewById<Button>(R.id.profileButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton) // New logout button

        heartRate.setOnClickListener {
            val i = Intent(this, HeartRateActivity::class.java)
            startActivity(i)
        }

        bmi.setOnClickListener {
            val i = Intent(this, BMIActivity::class.java)
            startActivity(i)
        }

        sensor.setOnClickListener {
            val i = Intent(this, Pedosensor::class.java)
            startActivity(i)
        }

        stepCount.setOnClickListener {
            val i = Intent(this, StepCount::class.java)
            startActivity(i)
        }

        profile.setOnClickListener {
            val i = Intent(this, report::class.java)
            startActivity(i)
        }

        logoutButton.setOnClickListener {
            // Clear user data, navigate to the login screen, etc.
            auth.signOut() // Sign out from Firebase

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val i = Intent(this, LoginActivity::class.java) // Replace with your login activity
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish() // Close the current activity
        }
    }
}
