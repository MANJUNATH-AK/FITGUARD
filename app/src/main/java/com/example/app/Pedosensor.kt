package com.example.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Pedosensor : AppCompatActivity(), SensorEventListener {

    private lateinit var interpreter: Interpreter
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val nFeatures = 15
    private val smoothingWindowSize = 10

    private val sensorDataBuffer = FloatArray(nFeatures)
    private val smoothedData = FloatArray(nFeatures)
    private val smoothingBuffer = Array(smoothingWindowSize) { FloatArray(nFeatures) }
    private var smoothingIndex = 0

    private lateinit var resultTextView: TextView
    private lateinit var sittingTimeTextView: TextView
    private lateinit var layingTimeTextView: TextView
    private lateinit var walkingUpstairsTimeTextView: TextView
    private lateinit var walkingDownstairsTimeTextView: TextView
    private lateinit var stepCountTextView: TextView

    private var totalSittingTime: Long = 0
    private var totalLayingTime: Long = 0
    private var totalWalkingUpstairsTime: Long = 0
    private var totalWalkingDownstairsTime: Long = 0

    private var lastActivity: Activity = Activity.UNKNOWN
    private var activityStartTime: Long = 0
    private var isTimerRunning: Boolean = false

    private enum class Activity {
        STANDING, SITTING, LAYING, WALKING, WALKING_DOWNSTAIRS, WALKING_UPSTAIRS, UNKNOWN
    }

    private val stepCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stepCount = intent?.getIntExtra("step_count", 0) ?: 0
            stepCountTextView.text = "Steps: $stepCount"
            updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedosensor)

        resultTextView = findViewById(R.id.resultTextView)
        sittingTimeTextView = findViewById(R.id.sittingCountTextView)
        layingTimeTextView = findViewById(R.id.layingCountTextView)
        walkingUpstairsTimeTextView = findViewById(R.id.walkingUpstairsCountTextView)
        walkingDownstairsTimeTextView = findViewById(R.id.walkingDownstairsCountTextView)

        interpreter = Interpreter(loadModelFile())
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val intent = Intent(this, StepCountService::class.java)
        startService(intent)

        // Check if user is logged in
        val user = auth.currentUser
        if (user != null) {
            fetchActivityDataFromFirestore(user.uid)
        } else {
            // Handle user not logged in, show login screen or relevant action
            // Redirect to login activity if needed
        }
    }

    private fun fetchActivityDataFromFirestore(userId: String) {
        val userActivityRef = firestore.collection("users").document(userId).collection("activity_data")

        userActivityRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                for (doc in snapshot.documents) {
                    totalSittingTime = doc.getLong("sitting_time") ?: 0
                    totalLayingTime = doc.getLong("laying_time") ?: 0
                    totalWalkingUpstairsTime = doc.getLong("walking_upstairs_time") ?: 0
                    totalWalkingDownstairsTime = doc.getLong("walking_downstairs_time") ?: 0
                }
            }
            updateUI()
        }.addOnFailureListener {
            // Initialize to zero if fetch fails
            totalSittingTime = 0
            totalLayingTime = 0
            totalWalkingUpstairsTime = 0
            totalWalkingDownstairsTime = 0
            updateUI()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        registerReceiver(stepCountReceiver, IntentFilter("com.example.fitguard.STEP_COUNT"))
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        unregisterReceiver(stepCountReceiver)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]

                sensorDataBuffer[0] = ax
                sensorDataBuffer[1] = ay
                sensorDataBuffer[2] = az

                applySmoothing(sensorDataBuffer)

                val result = predict(smoothedData)
                updateUIAndCounts(result)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun applySmoothing(data: FloatArray) {
        smoothingBuffer[smoothingIndex] = data.clone()
        smoothingIndex = (smoothingIndex + 1) % smoothingWindowSize

        for (i in smoothedData.indices) {
            smoothedData[i] = smoothingBuffer.map { it[i] }.average().toFloat()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun predict(sensorData: FloatArray): Int {
        val inputBuffer = ByteBuffer.allocateDirect(nFeatures * 4).order(ByteOrder.nativeOrder())
        sensorData.forEach { inputBuffer.putFloat(it) }

        val outputBuffer = ByteBuffer.allocateDirect(6 * 4).order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()

        val output = FloatArray(6)
        outputBuffer.asFloatBuffer().get(output)
        return output.indices.maxByOrNull { output[it] } ?: -1
    }

    private fun updateUIAndCounts(prediction: Int) {
        val currentActivity = when (prediction) {
            0 -> Activity.STANDING
            1 -> Activity.SITTING
            2 -> Activity.LAYING
            3 -> Activity.WALKING
            4 -> Activity.WALKING_DOWNSTAIRS
            5 -> Activity.WALKING_UPSTAIRS
            else -> Activity.UNKNOWN
        }

        if (currentActivity != lastActivity) {
            if (lastActivity != Activity.UNKNOWN && isTimerRunning) {
                val elapsedTime = (SystemClock.elapsedRealtime() - activityStartTime) / 1000
                updateCumulativeTime(lastActivity, elapsedTime)
            }
            lastActivity = currentActivity
            activityStartTime = SystemClock.elapsedRealtime()
            isTimerRunning = true
        }

        resultTextView.text = "Predicted Activity: ${currentActivity.name}"
        updateUI()
    }

    private fun updateCumulativeTime(activity: Activity, elapsedTime: Long) {
        when (activity) {
            Activity.SITTING -> totalSittingTime += elapsedTime
            Activity.LAYING -> totalLayingTime += elapsedTime
            Activity.WALKING_UPSTAIRS -> totalWalkingUpstairsTime += elapsedTime
            Activity.WALKING_DOWNSTAIRS -> totalWalkingDownstairsTime += elapsedTime
            else -> {}
        }
        saveDataToFirestore()
    }

    private fun saveDataToFirestore() {
        val userId = auth.currentUser?.uid ?: return // Ensure the user is logged in
        val activityData = hashMapOf(
            "sitting_time" to totalSittingTime,
            "laying_time" to totalLayingTime,
            "walking_upstairs_time" to totalWalkingUpstairsTime,
            "walking_downstairs_time" to totalWalkingDownstairsTime
        )

        firestore.collection("users").document(userId).collection("activity_data")
            .document("latest_activity")
            .set(activityData)
            .addOnSuccessListener {
                // Successfully saved data
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun updateUI() {
        sittingTimeTextView.text = "Sitting: ${formatTime(totalSittingTime)}"
        layingTimeTextView.text = "Laying: ${formatTime(totalLayingTime)}"
        walkingUpstairsTimeTextView.text = "Walking Upstairs: ${formatTime(totalWalkingUpstairsTime)}"
        walkingDownstairsTimeTextView.text = "Walking Downstairs: ${formatTime(totalWalkingDownstairsTime)}"
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes}m ${remainingSeconds}s"
    }
}
