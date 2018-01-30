package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import it.mindtek.ruah.db.converters.StringArrayConverter
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 21/12/2017.
 */
@Entity(tableName = "write")
open class ModelWrite(
        @PrimaryKey
        var id: Int = 0,
        var unit_id: Int = 0,
        var picture: String = "",
        var word: String = "",
        var type: String = "",
        var audio: String = "",
        @TypeConverters(StringArrayConverter::class)
        var letters: MutableList<Syllable> = mutableListOf()
) {}