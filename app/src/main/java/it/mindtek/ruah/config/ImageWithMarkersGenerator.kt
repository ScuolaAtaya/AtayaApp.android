package it.mindtek.ruah.config

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import androidx.core.content.ContextCompat
import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelMarker
import org.jetbrains.anko.dip
import java.io.File

object ImageWithMarkersGenerator {
    private lateinit var context: Context

    fun init(context: Context) {
        ImageWithMarkersGenerator.context = context.applicationContext
    }

    fun createImageWithMarkers(markerList: MutableList<ModelMarker>, file: File): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inMutable = true
        val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
        val canvas = Canvas(bitmap)
        markerList.forEach {
            createMarker(it, canvas)
        }
        return bitmap
    }

    private fun createMarker(marker: ModelMarker, canvas: Canvas) {
        val radius = context.dip(12).toFloat()
        val paint = Paint()
        paint.color = ContextCompat.getColor(context, R.color.blue)
        canvas.drawCircle(context.dip(10).toFloat(), context.dip(10).toFloat(), radius, paint)
        val paintText = TextPaint()
        paintText.color = ContextCompat.getColor(context, R.color.white)
        paintText.textSize = context.dip(16).toFloat()
        paintText.textAlign = Paint.Align.CENTER
        val textHeight: Float = paintText.descent() - paintText.ascent()
        val textOffset: Float = textHeight / 2 - paintText.descent()
        canvas.drawText("1", context.dip(10).toFloat(), context.dip(10).toFloat() + textOffset, paintText)
    }
}