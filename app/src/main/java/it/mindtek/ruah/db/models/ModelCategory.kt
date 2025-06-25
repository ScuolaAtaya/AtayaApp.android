package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
open class ModelCategory(
    @PrimaryKey val id: Int,
    val name: String,
    val position: Int,
)