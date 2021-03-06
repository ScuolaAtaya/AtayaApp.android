package it.mindtek.ruah.db.models

import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.IntArrayConverter

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class ModelSyllable(
        var id: String = "",
        var text: String = "",
        @TypeConverters(IntArrayConverter::class)
        var occurences: MutableList<Int> = mutableListOf(),
        var enabled: Boolean = true
)