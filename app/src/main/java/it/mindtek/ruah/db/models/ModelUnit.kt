package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.IntArrayConverter

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Entity(tableName = "units")
open class ModelUnit(
    @PrimaryKey val id: Int,
    val name: String,
    val position: Int,
    val advanced: Boolean = false,
    val enabled: Boolean = false,
    @param:TypeConverters(IntArrayConverter::class) val completed: MutableList<Int> = mutableListOf()
)