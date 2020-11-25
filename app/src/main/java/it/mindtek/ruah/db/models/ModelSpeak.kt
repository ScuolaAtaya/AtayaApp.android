package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Entity(tableName = "speak")
open class ModelSpeak(
        @PrimaryKey
        var id: String,
        var unit_id: Int,
        @Embedded(prefix = "audio_")
        var audio: ModelMedia,
        @Embedded(prefix = "picture_")
        var picture: ModelMedia
)