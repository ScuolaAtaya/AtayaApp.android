package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "final")
open class ModelFinalTest(@PrimaryKey val id: String, val unit_id: Int)