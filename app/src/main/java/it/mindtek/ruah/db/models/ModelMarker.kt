package it.mindtek.ruah.db.models

import androidx.room.PrimaryKey

class ModelMarker (
        @PrimaryKey
        var id: String = "",
        var section_id: String = "",
        var x: Double = 0.0,
        var y: Double = 0.0
)