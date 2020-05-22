package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Entity(tableName = "speak")
open class ModelSpeak(
        @PrimaryKey
        var id: String = "",
        var unit_id: Int = 0,
        var picture: String = "",
        var audio: String = ""
) {}