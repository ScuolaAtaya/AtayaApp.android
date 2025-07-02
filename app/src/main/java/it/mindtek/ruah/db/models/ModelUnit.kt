package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.IntArrayConverter

@Entity(tableName = "units")
open class ModelUnit(
    @PrimaryKey val id: Int,
    val category: String,
    val name: String,
    val position: Int,
    val advanced: Boolean = false,
    val enabled: Boolean = false,
    @param:TypeConverters(IntArrayConverter::class) val completed: MutableList<Int> = mutableListOf()
)