package it.mindtek.ruah.db.models

import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.IntArrayConverter

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class ModelSyllable(
    val id: String = "",
    val text: String = "",
    @param:TypeConverters(IntArrayConverter::class) val occurences: MutableList<Int> = mutableListOf(),
    val enabled: Boolean = true
)