package com.example.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepCount : AppCompatActivity() {

    private lateinit var stepCountTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var caloriesTextView: TextView
    private lateinit var stepCountCard: CardView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var stepCount = 0

    private val stepCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            stepCount = intent?.getIntExtra("step_count", 0) ?: 0
            val timestamp = intent?.getLongExtra("timestamp", 0) ?: 0

            stepCountTextView.text = getString(R.string.steps_format, stepCount)
            val distanceInKm = calculateDistance(stepCount)
            val caloriesBurned = calculateCalories(stepCount)

            distanceTextView.text = getString(R.string.distance_format, distanceInKm)
            caloriesTextView.text = getString(R.string.calories_format, caloriesBurned)

            saveStepCountToFirestore(StepData(stepCount, distanceInKm, caloriesBurned, timestamp))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_count)

        stepCountTextView = findViewById(R.id.stepCountTextViewUnique)
        distanceTextView = findViewById(R.id.distanceTextViewUnique)
        caloriesTextView = findViewById(R.id.caloriesTextViewUnique)
        stepCountCard = findViewById(R.id.stepCountCardUnique)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val serviceIntent = Intent(this, StepCountService::class.java)
        startForegroundService(serviceIntent)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(stepCountReceiver, IntentFilter("com.example.app.STEP_COUNT"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(stepCountReceiver)
    }

    private fun calculateDistance(steps: Int): Double {
        val stepLength = 0.0008 // Approximate step length in km
        return steps * stepLength
    }

    private fun calculateCalories(steps: Int): Double {
        val caloriesPerStep = 0.04 // Approximate calories burned per step
        return steps * caloriesPerStep
    }

    private fun saveStepCountToFirestore(stepData: StepData) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).collection("steps")
            .add(stepData)
            .addOnSuccessListener {
                // Successfully saved step data to Firestore
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
}


