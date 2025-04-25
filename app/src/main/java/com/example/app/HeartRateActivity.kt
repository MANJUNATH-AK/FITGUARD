package com.example.app

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.os.CountDownTimer
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.max
import kotlin.math.min

class HeartRateActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var surfaceView: SurfaceView
    private lateinit var textView: TextView
    private lateinit var checkHeartRateButton: Button
    private lateinit var checkBloodOxygenButton: Button
    private lateinit var progressBar: ProgressBar
    private var camera: Camera? = null
    private var redIntensityValues = mutableListOf<Double>()
    private var frameCount = 0
    private val totalTime = 30000L // 30 seconds for heart rate sensing
    private val frameRate = 30 // Approximate frame rate (FPS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        surfaceView = findViewById(R.id.surfaceView)
        textView = findViewById(R.id.textView)
        checkHeartRateButton = findViewById(R.id.checkHeartRateButton)
        checkBloodOxygenButton = findViewById(R.id.checkBloodOxygenButton)
        progressBar = findViewById(R.id.progressBar)

        val holder: SurfaceHolder = surfaceView.holder
        holder.addCallback(this)

        checkHeartRateButton.setOnClickListener {
            startHeartRateMeasurement()
        }

        checkBloodOxygenButton.setOnClickListener {
            startBloodOxygenMeasurement()
        }

        // Check for camera permission
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }
    }

    private fun startHeartRateMeasurement() {
        checkHeartRateButton.isEnabled = false // Disable button during sensing
        progressBar.progress = 100 // Set progress to 100% at the start
        redIntensityValues.clear() // Clear previous data

        // Turn on the flashlight
        turnOnFlashlight()

        // Start capturing video frames for analysis
        camera?.setPreviewCallback { data, _ ->
            if (data != null) {
                val redIntensity = extractRedIntensityFromFrame(data)
                redIntensityValues.add(redIntensity)
                frameCount++
            }
        }

        // Countdown timer for 30 seconds
        object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingTime = millisUntilFinished / 1000
                val progress = (millisUntilFinished.toFloat() / totalTime * 100).toInt()
                textView.text = "Checking heart rate... Time left: ${remainingTime}s"
                progressBar.progress = progress // Update progress bar
            }

            override fun onFinish() {
                camera?.setPreviewCallback(null) // Stop capturing frames
                turnOffFlashlight() // Turn off flashlight
                calculateHeartRate()
                progressBar.progress = 0 // Reset progress bar
                checkHeartRateButton.isEnabled = true // Enable button after sensing
            }
        }.start()
    }

    private fun startBloodOxygenMeasurement() {
        checkBloodOxygenButton.isEnabled = false // Disable button during sensing
        progressBar.progress = 100 // Set progress to 100% at the start
        redIntensityValues.clear() // Clear previous data

        // Turn on the flashlight
        turnOnFlashlight()

        // Start capturing video frames for analysis
        camera?.setPreviewCallback { data, _ ->
            if (data != null) {
                val redIntensity = extractRedIntensityFromFrame(data)
                redIntensityValues.add(redIntensity)
                frameCount++
            }
        }

        // Countdown timer for 30 seconds
        object : CountDownTimer(totalTime, 3000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingTime = millisUntilFinished / 1000
                val progress = (millisUntilFinished.toFloat() / totalTime * 100).toInt()
                textView.text = "Checking blood oxygen saturation... Time left: ${remainingTime}s"
                progressBar.progress = progress // Update progress bar
            }

            override fun onFinish() {
                camera?.setPreviewCallback(null) // Stop capturing frames
                turnOffFlashlight() // Turn off flashlight
                calculateBloodOxygenSaturation()
                progressBar.progress = 0 // Reset progress bar
                checkBloodOxygenButton.isEnabled = true // Enable button after sensing
            }
        }.start()
    }

    private fun turnOnFlashlight() {
        if (camera == null) {
            camera = Camera.open()
        }
        val params = camera?.parameters
        params?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        camera?.parameters = params
        camera?.startPreview()
    }

    private fun turnOffFlashlight() {
        camera?.let {
            val params = it.parameters
            params.flashMode = Camera.Parameters.FLASH_MODE_OFF
            it.parameters = params
            it.stopPreview()
        }
    }

    private fun extractRedIntensityFromFrame(data: ByteArray): Double {
        // Convert the frame data to YUV format and extract red channel intensity
        var sumRed = 0
        for (i in data.indices step 4) {
            sumRed += data[i].toInt() and 0xFF // Extract red component
        }
        return sumRed.toDouble() / (data.size / 4) // Average red intensity
    }

    private fun calculateHeartRate() {
        if (redIntensityValues.isEmpty()) {
            textView.text = "Unable to detect heart rate"
            return
        }

        // Step 1: Apply a simple moving average to smooth the data
        val smoothedData = movingAverage(redIntensityValues, 5)

        // Step 2: Detect peaks based on a threshold
        val peaks = detectPeaks(smoothedData)

        // Step 3: Calculate the heart rate based on peak detection
        val numBeats = peaks.size
        val durationInSeconds = totalTime / 1000.0
        val heartRate = (numBeats.toDouble() / durationInSeconds) * 52 // BPM

        if (heartRate > 40 && heartRate < 200) { // Filter unreasonable values
            textView.text = "Heart Rate: ${heartRate.toInt()} bpm"
        } else {
            textView.text = "Unable to detect accurate heart rate"
        }
    }

    private fun calculateBloodOxygenSaturation() {
        if (redIntensityValues.isEmpty()) {
            textView.text = "Unable to detect blood oxygen saturation"
            return
        }

        // For simplicity, let's assume a simple calculation based on red intensity
        val averageRedIntensity = redIntensityValues.average()
        val spo2 = (averageRedIntensity / 120.0) * 100 // Convert to percentage (0-100%)

        if (spo2 >= 90) { // Check for normal values
            textView.text = "Blood Oxygen Saturation: ${spo2.toInt()}%"
        } else {
            textView.text = "Low Blood Oxygen Saturation: ${spo2.toInt()}%"
        }
    }

    // Moving Average function to smooth the signal
    private fun movingAverage(data: List<Double>, windowSize: Int): List<Double> {
        val smoothedData = mutableListOf<Double>()
        for (i in data.indices) {
            val windowStart = max(0, i - windowSize / 2)
            val windowEnd = min(data.size, i + windowSize / 2 + 1)
            val window = data.subList(windowStart, windowEnd)
            smoothedData.add(window.average())
        }
        return smoothedData
    }

    // Peak detection function (simple example)
    private fun detectPeaks(data: List<Double>): List<Int> {
        val peaks = mutableListOf<Int>()
        for (i in 1 until data.size - 1) {
            if (data[i] > data[i - 1] && data[i] > data[i + 1]) {
                peaks.add(i)
            }
        }
        return peaks
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (camera == null) {
            camera = Camera.open()
        }
        camera?.setPreviewDisplay(holder)
        camera?.startPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can initialize the camera
                surfaceView.holder.addCallback(this)
            } else {
                textView.text = "Camera permission is required to use this feature"
            }
        }
    }
}
