package it.mindtek.ruah.config

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import it.mindtek.ruah.db.models.ModelMarker
import org.jetbrains.anko.dip
import java.io.File

object ImageWithMarkersGenerator {
    private lateinit var context: Context

    fun init(context: Context){
        ImageWithMarkersGenerator.context = context.applicationContext
    }

    fun createImageWithMarkers(markerList: MutableList<ModelMarker>, file: File): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inMutable = true
        val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
        val canvas = Canvas(bitmap)
        markerList.forEach {
            createMarker(it)
        }
        return bitmap
    }

    private fun createMarker(marker: ModelMarker) {
        val paint = Paint()
        paint.color = Color.WHITE
    }

    private fun setText(): TextPaint {
        val paintText = TextPaint()
        paintText.color = Color.BLUE
        paintText.textSize = context.dip(16).toFloat()
        paintText.textAlign = Paint.Align.CENTER
        return paintText
    }
}