package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "final")
open class ModelFinalTest (
        @PrimaryKey
        var id: String = "",
        var unit_id: Int = 0
)