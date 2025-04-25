package com.example.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BMIActivity : AppCompatActivity() {
    private lateinit var eTHeight: EditText
    private lateinit var eTWeight: EditText
    private lateinit var eTAge: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var btnCalculate: Button
    private lateinit var tvResult: TextView
    private lateinit var tvHealthAdvice: TextView
    private lateinit var lineChart: LineChart

    // Example to hold BMI values over days
    private val bmiEntries = mutableListOf<Entry>()
    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        eTHeight = findViewById(R.id.eTHeight)
        eTWeight = findViewById(R.id.eTWeight)
        eTAge = findViewById(R.id.eTAge)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        btnCalculate = findViewById(R.id.btnCalculate)
        tvResult = findViewById(R.id.tvResult)
        tvHealthAdvice = findViewById(R.id.tvHealthAdvice)
        lineChart = findViewById(R.id.lineChart)

        btnCalculate.setOnClickListener {
            calculateBMR()
        }

        // Retrieve BMI data from Firestore on Activity startup
        fetchBMIData()
    }

    private fun calculateBMR() {
        val height = eTHeight.text.toString().toFloatOrNull() ?: return
        val weight = eTWeight.text.toString().toFloatOrNull() ?: return
        val age = eTAge.text.toString().toIntOrNull() ?: return
        val isMale = findViewById<RadioButton>(R.id.radioMale).isChecked

        val bmr = if (isMale) {
            66 + (6.23 * weight) + (12.7 * height) - (6.8 * age)
        } else {
            655 + (4.35 * weight) + (4.7 * height) - (4.7 * age)
        }

        tvResult.text = "BMR: ${bmr.toInt()} calories/day"

        // Calculate BMI (weight in kg and height in cm)
        val bmi = weight / ((height / 100) * (height / 100))

        // Create a timestamp for the current BMI entry
        val timestamp = System.currentTimeMillis()

        // Get the current user ID
        val userId = auth.currentUser?.uid ?: return

        // Create BMIData object
        val bmiData = BMIData(bmi, timestamp)

        // Save the BMI data with timestamp for the logged-in user
        saveBMIDataToFirestore(userId, bmiData)

        // Add the BMI for line chart
        val dayIndex = bmiEntries.size + 1 // Example index
        bmiEntries.add(Entry(dayIndex.toFloat(), bmi.toFloat()))

        // Update LineChart
        updateLineChart()

        // Provide health advice based on BMI
        provideHealthAdvice(bmi)
    }

    private fun saveBMIDataToFirestore(userId: String, bmiData: BMIData) {
        // Save data under the user's document in Firestore
        db.collection("users")
            .document(userId) // User-specific document
            .collection("bmi_data") // Collection of BMI data for the user
            .add(bmiData)
            .addOnSuccessListener {
                // Data saved successfully
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun fetchBMIData() {
        val userId = auth.currentUser?.uid ?: return

        // Fetch BMI data from Firestore for the logged-in user
        db.collection("users")
            .document(userId) // User-specific document
            .collection("bmi_data") // Collection of BMI data for the user
            .orderBy("timestamp") // Sort by timestamp
            .get()
            .addOnSuccessListener { documents ->
                bmiEntries.clear() // Clear the previous data

                for (document in documents) {
                    val bmi = document.getDouble("bmi")?.toFloat() ?: 0f
                    val timestamp = document.getLong("timestamp") ?: 0L
                    val dayIndex = bmiEntries.size + 1
                    bmiEntries.add(Entry(dayIndex.toFloat(), bmi))
                }

                // Update LineChart with fetched data
                updateLineChart()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun updateLineChart() {
        val lineDataSet = LineDataSet(bmiEntries, "BMI Over Time")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Refresh the chart
    }

    private fun provideHealthAdvice(bmi: Float) {
        when {
            bmi < 18.5 -> tvHealthAdvice.text = "Underweight: Consider gaining weight."
            bmi in 18.5..24.9 -> tvHealthAdvice.text = "Normal weight: Maintain your current lifestyle."
            bmi in 25.0..29.9 -> tvHealthAdvice.text = "Overweight: Consider losing weight."
            else -> tvHealthAdvice.text = "Obesity: Consult a healthcare provider."
        }
    }
}
