package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.StringArrayConverter
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 21/12/2017.
 */
@Entity(tableName = "write")
open class ModelWrite(
        @PrimaryKey
        var id: String = "",
        var unit_id: Int = 0,
        var picture: String = "",
        var word: String = "",
        var type: String = "",
        var audio: String = "",
        @TypeConverters(StringArrayConverter::class)
        var letters: MutableList<Syllable> = mutableListOf()
) {}