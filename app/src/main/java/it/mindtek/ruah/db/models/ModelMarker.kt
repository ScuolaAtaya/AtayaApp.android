package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marker")
class ModelMarker (
        @PrimaryKey
        var id: String = "",
        var section_id: String = "",
        var x: Double = 0.0,
        var y: Double = 0.0
)