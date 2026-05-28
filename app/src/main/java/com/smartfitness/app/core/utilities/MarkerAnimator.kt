package com.smartfitness.app.core.utilities

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.LatLng

class MarkerAnimator {

    fun animate(
        start: LatLng,
        end: LatLng,
        duration: Long = 1000,
        onUpdate: (LatLng) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        val startTime = SystemClock.uptimeMillis()
        val interpolator = LinearInterpolator()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - startTime
                val t = (elapsed.toFloat() / duration).coerceAtMost(1f)

                val lat = start.latitude + (end.latitude - start.latitude) * t
                val lng = start.longitude + (end.longitude - start.longitude) * t

                onUpdate(LatLng(lat, lng))

                if (t < 1f) handler.postDelayed(this, 16)
            }
        })
    }
}