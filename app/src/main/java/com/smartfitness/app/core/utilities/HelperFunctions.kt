package com.smartfitness.app.core.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.util.Base64
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.smartfitness.app.domain.model.QuickAction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object HelperFunctions {

    val quickActions = listOf(
        QuickAction("Workout Plan", "Suggest me a workout plan"),
        QuickAction("Diet Plan", "Create a diet plan for me"),
        QuickAction("Lose Weight", "How can I lose weight fast?"),
        QuickAction("Gain Muscle", "How to gain muscle effectively?")
    )
     val EMOJIS = listOf(
        "😀","😂","😍","🥰","😎","😢","😡","🤔","🙌","👏",
        "👍","👎","🔥","❤️","💪","🏋️","🏃","🧘","🥗","🍎",
        "🥦","💧","😴","🎯","⚡","🏆","✅","🎉","💯","🤩",
        "😅","😇","🤗","🫡","💀","🤯","🥳","😤","🙏","💬"
    )


    fun uriToBase64(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


    /** Returns "Today", "Yesterday", or a formatted date string for a timestamp. */
    fun dateLabelFor(timestamp: Long): String {
        val msgCal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val todayCal = Calendar.getInstance()

        return when {
            isSameDay(msgCal, todayCal) -> "Today"
            isYesterday(msgCal, todayCal) -> "Yesterday"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

     fun isSameDay(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

     fun isYesterday(msgCal: Calendar, today: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(msgCal, yesterday)
    }

    fun formatTime(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return "%02d:%02d".format(min, sec)
    }


    fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)!!
        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        val bitmap = createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun resizedMarkerIcon(
        context: Context,
        drawableId: Int,
        width: Int,
        height: Int
    ): BitmapDescriptor {

        val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
        val smallBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

        return BitmapDescriptorFactory.fromBitmap(smallBitmap)
    }
    fun getBearing(start: LatLng, end: LatLng): Float {
        val lat1 = Math.toRadians(start.latitude)
        val lng1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lng2 = Math.toRadians(end.longitude)

        val dLng = lng2 - lng1
        val y = kotlin.math.sin(dLng) * kotlin.math.cos(lat2)
        val x = kotlin.math.cos(lat1) * kotlin.math.sin(lat2) -
                kotlin.math.sin(lat1) * kotlin.math.cos(lat2) * kotlin.math.cos(dLng)

        var bearing = Math.toDegrees(kotlin.math.atan2(y, x)).toFloat()
        
        // Adjust for icon orientation if it's facing opposite (add 180 degrees)
        bearing = (bearing + 180) % 360
        
        return bearing
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return poly
    }
}