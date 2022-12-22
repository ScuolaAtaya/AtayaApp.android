package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mindtek.ruah.db.converters.IntArrayConverter

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Entity(tableName = "units")
open class ModelUnit(
        @PrimaryKey
        var id: Int,
        var name: String,
        var position: Int,
        var advanced: Boolean = false,
        var enabled: Boolean = false,
        @TypeConverters(IntArrayConverter::class)
        var completed: MutableList<Int> = mutableListOf()
)