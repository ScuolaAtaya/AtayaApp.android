package it.mindtek.ruah.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import it.mindtek.ruah.kotlin.extensions.fromJson

/**
 * Created by alessandrogaboardi on 15/01/2018.
 */
class IntArrayConverter {
    @TypeConverter
    fun toArray(string: String): MutableList<Int> = Gson().fromJson(string)

    @TypeConverter
    fun toJson(mutableList: MutableList<Int>): String = Gson().toJson(mutableList)
}