package com.example.app

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Calendar

class report : AppCompatActivity() {

    private lateinit var dailyStepsLineChart: LineChart
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var dayButton: Button
    private lateinit var weekButton: Button
    private lateinit var monthButton: Button

    private val targetSteps = 8000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        dailyStepsLineChart = findViewById(R.id.stepChart)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        dayButton = findViewById(R.id.dayButton)
        weekButton = findViewById(R.id.weekButton)
        monthButton = findViewById(R.id.monthButton)

        dayButton.setOnClickListener { fetchStepData("day") }
        weekButton.setOnClickListener { fetchStepData("week") }
        monthButton.setOnClickListener { fetchStepData("month") }

        styleChart() // Apply custom styles to the chart
        fetchStepData("day") // Default to fetch daily data
    }

    private fun fetchStepData(period: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("AuthError", "User is not authenticated.")
            return
        }

        val startDate = getStartDateForPeriod(period)

        firestore.collection("users").document(userId).collection("steps")
            .whereGreaterThanOrEqualTo("timestamp", startDate)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    parseAndPlotStepData(snapshot) // Parse and plot data
                } else {
                    Log.d("FirestoreData", "No step data found for selected period")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Data retrieval failed: ${e.message}")
            }
    }

    private fun getStartDateForPeriod(period: String): Long {
        val calendar = Calendar.getInstance()
        when (period) {
            "day" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "week" -> {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        return calendar.timeInMillis
    }

    private fun parseAndPlotStepData(snapshot: QuerySnapshot) {
        val entries = ArrayList<Entry>() // List to store entries for chart

        for (document in snapshot.documents) {
            val stepData = document.toObject(StepData::class.java) // Convert Firestore document to StepData object
            stepData?.let { data ->
                entries.add(Entry(data.timestamp.toFloat(), data.steps.toFloat())) // Add entry to the chart data
            }
        }

        if (entries.isNotEmpty()) {
            plotStepData(entries)
        }
    }

    private fun plotStepData(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Daily Steps").apply {
            color = ContextCompat.getColor(this@report, R.color.teal_200) // Line color
            valueTextColor = ContextCompat.getColor(this@report, R.color.black) // Data point text color
            valueTextSize = 10f // Data point text size
            lineWidth = 2f // Line thickness
            setCircleColor(ContextCompat.getColor(this@report, R.color.purple_500)) // Circle color for data points
            circleRadius = 5f // Circle size
            setDrawFilled(true) // Fill the area under the line
            fillColor = ContextCompat.getColor(this@report, R.color.teal_200) // Fill color
            mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth curves
        }

        val lineData = LineData(dataSet)
        dailyStepsLineChart.apply {
            data = lineData
            invalidate() // Refresh the chart to display data
        }
    }

    private fun styleChart() {
        dailyStepsLineChart.apply {
            setBackgroundColor(ContextCompat.getColor(this@report, R.color.white)) // Background color
            setDrawGridBackground(false) // No grid background
            setDrawBorders(true) // Draw chart borders
            setBorderColor(ContextCompat.getColor(this@report, R.color.light_gray)) // Border color
            setBorderWidth(1f)

            xAxis.apply {
                textColor = ContextCompat.getColor(this@report, R.color.black)
                textSize = 12f
                setDrawAxisLine(true)
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textColor = ContextCompat.getColor(this@report, R.color.black)
                textSize = 12f
                setDrawAxisLine(true)
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false // Disable right axis

            legend.apply {
                textColor = ContextCompat.getColor(this@report, R.color.black)
                textSize = 14f
                form = Legend.LegendForm.LINE // Style the legend
            }

            animateX(1000)
            animateY(1000)

            description.apply {
                text = "Daily Step Count"
                textColor = ContextCompat.getColor(this@report, R.color.purple_500)
                textSize = 14f
            }
        }
    }
}
