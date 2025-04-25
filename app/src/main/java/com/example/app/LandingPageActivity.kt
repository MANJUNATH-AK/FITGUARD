package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LandingPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Find the start button by its ID
        val startButton: Button = findViewById(R.id.start_button)

        // Set an OnClickListener to the start button
        startButton.setOnClickListener {
            // Create an Intent to navigate to the MainActivity
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
