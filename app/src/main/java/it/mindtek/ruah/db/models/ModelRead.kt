package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.MarkerArrayConverter
import it.mindtek.ruah.db.converters.StringArrayConverter

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "read")
open class ModelRead(
        @PrimaryKey
        var id: String,
        var unit_id: Int,
        @Embedded(prefix = "picture_")
        var picture: ModelMedia,
        @TypeConverters(MarkerArrayConverter::class)
        var markers: MutableList<ModelMarker> = mutableListOf()
)