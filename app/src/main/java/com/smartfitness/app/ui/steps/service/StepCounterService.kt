package com.smartfitness.app.ui.steps.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartfitness.app.R
import com.smartfitness.app.ui.steps.data.StepDao
import com.smartfitness.app.ui.steps.data.StepRecord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var stepDao: StepDao

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // The hardware step counter is cumulative since last boot.
    // We store the initial value on first event to compute today's delta.
    private var sensorBaseline = -1f

    companion object {
        const val CHANNEL_ID = "step_counter_channel"
        const val NOTIFICATION_ID = 2001
        const val ACTION_STOP = "ACTION_STOP_STEP_SERVICE"

        // Shared steps count so ViewModel can read it without DB round-trip
        var todaySteps: Int = 0
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return
        val totalSinceboot = event.values[0]

        if (sensorBaseline < 0f) {
            // First read — restore baseline from DB so we don't count yesterday's steps
            serviceScope.launch {
                val today = todayDate()
                val existing = stepDao.getByDate(today)
                sensorBaseline = totalSinceboot - (existing?.steps ?: 0)
            }
            return
        }

        val steps = (totalSinceboot - sensorBaseline).toInt().coerceAtLeast(0)
        todaySteps = steps

        // Update notification
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(steps))

        // Persist to Room DB
        serviceScope.launch {
            stepDao.upsert(StepRecord(date = todayDate(), steps = steps))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        serviceScope.launch { /* final save already handled by onSensorChanged */ }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun todayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Counts your daily steps" }
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(steps: Int): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SmartFitness")
            .setContentText("Today's steps: $steps")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setSilent(true)
            .build()
}


