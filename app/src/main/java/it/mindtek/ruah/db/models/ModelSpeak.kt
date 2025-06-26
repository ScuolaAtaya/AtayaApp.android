package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Entity(tableName = "speak")
open class ModelSpeak(
    @PrimaryKey val id: String,
    val unit_id: Int,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    @Embedded(prefix = "picture_") val picture: ModelMedia
)