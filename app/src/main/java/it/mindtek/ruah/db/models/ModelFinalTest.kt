package it.mindtek.ruah.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "final")
open class ModelFinalTest(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "unit_id") val unitId: Int
)