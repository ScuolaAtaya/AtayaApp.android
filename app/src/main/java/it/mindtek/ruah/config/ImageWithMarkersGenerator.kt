package it.mindtek.ruah.config

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import androidx.core.content.ContextCompat
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelMarker
import java.io.File

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
        markerList.forEach {
            createMarker(it, canvas)
        }
        return bitmap
    }

    private fun createMarker(marker: ModelMarker, canvas: Canvas) {
        // Calculate the smallest side of the canvas in density-independent pixels (dp) to use as a scaling ratio
        val scalingRatio: Float =
            (if (canvas.width > canvas.height) canvas.height else canvas.width).toFloat() / canvas.density
        val radius: Float = LayoutUtils.dpToPx(context, 14) * scalingRatio
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
            textSize = LayoutUtils.spToPx(context, 16) * scalingRatio
            textAlign = Paint.Align.CENTER
        }
        val textOffset: Float = (paintText.descent() - paintText.ascent()) / 2 - paintText.descent()
        canvas.drawText(marker.id, x, y + textOffset, paintText)
    }
}