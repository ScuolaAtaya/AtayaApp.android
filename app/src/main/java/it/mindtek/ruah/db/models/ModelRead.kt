package it.mindtek.ruah.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.MarkerArrayConverter

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "read")
open class ModelRead(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @Embedded(prefix = "picture_") val picture: ModelMedia,
    @param:TypeConverters(MarkerArrayConverter::class) val markers: MutableList<ModelMarker> = mutableListOf()
)