package it.mindtek.ruah.config

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.ContextCompat
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelMarker
import java.io.File


@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
object ImageWithMarkersGenerator {
    private lateinit var context: Context

    fun init(context: Context) {
        ImageWithMarkersGenerator.context = context.applicationContext
    }

    fun createImageWithMarkers(markerList: MutableList<ModelMarker>, file: File): Bitmap? {
        val bitmap: Bitmap =
            BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
                inMutable = true
            }) ?: return null
        val canvas = Canvas(bitmap)
        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val widthPixels: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) wm.currentWindowMetrics.bounds.width() else {
                val displayMetrics = DisplayMetrics()
                wm.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        markerList.forEach {
            createMarker(it, canvas, widthPixels)
        }
        return bitmap
    }

    private fun createMarker(marker: ModelMarker, canvas: Canvas, width: Int) {
        val radius: Float = (LayoutUtils.dpToPx(context, 14) * canvas.width / width).toFloat()
        var x: Float = (marker.x * canvas.width).toFloat()
        var y: Float = (marker.y * canvas.height).toFloat()
        x = when {
            x < radius -> radius
            x > canvas.width - radius -> canvas.width - radius
            else -> x
        }
        y = when {
            y < radius -> radius
            y > canvas.height - radius -> canvas.height - radius
            else -> y
        }
        canvas.drawCircle(x, y, radius, Paint().apply {
            color = ContextCompat.getColor(context, R.color.blue)
            isAntiAlias = true
        })
        val paintText: TextPaint = TextPaint().apply {
            color = ContextCompat.getColor(context, R.color.white)
            textSize = LayoutUtils.spToPx(context, 16).toFloat()
            textAlign = Paint.Align.CENTER
        }
        val textOffset: Float = (paintText.descent() - paintText.ascent()) / 2 - paintText.descent()
        canvas.drawText(marker.id, x, y + textOffset, paintText)
    }
}