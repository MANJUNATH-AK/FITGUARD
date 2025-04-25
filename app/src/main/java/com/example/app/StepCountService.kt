package com.example.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class StepCountService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastAccelZValue = -9999.0
    private var stepCount = 0

    private val highThreshold = 1.2
    private val lowThreshold = -1.2
    private val lowPassFilterAlpha = 0.85
    private var isPeakDetected = false

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        startForegroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun startForegroundService() {
        val channelId = "step_count_channel"
        val channelName = "Step Count Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Step Counter Running")
            .setContentText("Step counting is active in the background.")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        startForeground(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val zValue = event.values[2]
            val filteredZValue = (lowPassFilterAlpha * lastAccelZValue) + (1 - lowPassFilterAlpha) * zValue
            detectStep(filteredZValue)
            lastAccelZValue = filteredZValue
        }
    }

    private fun detectStep(zValue: Double) {
        if (!isPeakDetected && zValue > highThreshold) {
            isPeakDetected = true
        }

        if (isPeakDetected && zValue < lowThreshold) {
            stepCount++
            isPeakDetected = false
            broadcastStepCount(stepCount)
        }
    }

    private fun broadcastStepCount(stepCount: Int) {
        val intent = Intent("com.example.app.STEP_COUNT")
        intent.putExtra("step_count", stepCount)
        intent.putExtra("timestamp", System.currentTimeMillis())
        sendBroadcast(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
