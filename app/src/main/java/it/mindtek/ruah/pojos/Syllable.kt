package it.mindtek.ruah.pojos

import android.arch.persistence.room.TypeConverters
import it.mindtek.ruah.db.converters.IntArrayConverter

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
class Syllable(
        var id: Int = 0,
        var text: String = "",
        @TypeConverters(IntArrayConverter::class)
        var order: MutableList<Int> = mutableListOf(),
        var enabled: Boolean = true
) {}