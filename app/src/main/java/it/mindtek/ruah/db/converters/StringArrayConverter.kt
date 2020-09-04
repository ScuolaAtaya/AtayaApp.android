package it.mindtek.ruah.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import it.mindtek.ruah.kotlin.extensions.fromJson
import it.mindtek.ruah.db.models.ModelSyllable

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class StringArrayConverter {
    @TypeConverter
    fun toArray(string: String): MutableList<ModelSyllable> {
        return Gson().fromJson(string)
    }

    @TypeConverter
    fun toJson(mutableList: MutableList<ModelSyllable>): String = Gson().toJson(mutableList)
}