package it.mindtek.ruah.db.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import it.mindtek.ruah.kotlin.extensions.fromJson
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class StringArrayConverter {
    @TypeConverter
    fun toArray(string: String): MutableList<Syllable> {
        return Gson().fromJson<MutableList<Syllable>>(string)
    }

    @TypeConverter
    fun toJson(mutableList: MutableList<Syllable>): String = Gson().toJson(mutableList)
}