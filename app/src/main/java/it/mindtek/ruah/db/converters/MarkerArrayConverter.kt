package it.mindtek.ruah.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import it.mindtek.ruah.db.models.ModelMarker
import it.mindtek.ruah.kotlin.extensions.fromJson

class MarkerArrayConverter {
    @TypeConverter
    fun toArray(string: String): MutableList<ModelMarker> = Gson().fromJson(string)

    @TypeConverter
    fun toJson(mutableList: MutableList<ModelMarker>): String = Gson().toJson(mutableList)
}