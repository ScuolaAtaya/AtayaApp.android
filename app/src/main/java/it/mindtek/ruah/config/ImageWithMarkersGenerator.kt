package it.mindtek.ruah.config

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.DisplayMetrics
import android.view.WindowManager
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
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inMutable = true
        val bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return null
        val canvas = Canvas(bitmap)
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(displayMetrics)
        markerList.forEach {
            createMarker(it, canvas, displayMetrics.widthPixels)
        }
        return bitmap
    }

    private fun createMarker(marker: ModelMarker, canvas: Canvas, width: Int) {
        val radius = (LayoutUtils.dpToPx(context, 14) * canvas.width / width).toFloat()
        var x = (marker.x * canvas.width).toFloat()
        var y = (marker.y * canvas.height).toFloat()
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
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.blue)
        paint.isAntiAlias = true
        canvas.drawCircle(x, y, radius, paint)
        val paintText = TextPaint()
        paintText.color = ContextCompat.getColor(context, R.color.white)
        paintText.textSize = LayoutUtils.spToPx(context, 16).toFloat()
        paintText.textAlign = Paint.Align.CENTER
        val textOffset: Float = (paintText.descent() - paintText.ascent()) / 2 - paintText.descent()
        canvas.drawText(marker.id, x, y + textOffset, paintText)
    }
}