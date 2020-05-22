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
        var id: Int = 0,
        @DrawableRes
        var icon: Int = 0,
        @StringRes
        var name: Int = 0,
        var color: Int = 0,
        var colorDark: Int = 0,
        var position: Int = 0,
        var advanced: Boolean = false,
        var enabled: Boolean = false,
        @TypeConverters(IntArrayConverter::class)
        var completed: MutableList<Int> = mutableListOf()
) {}