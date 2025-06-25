package it.mindtek.ruah.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Entity(tableName = "speak")
open class ModelSpeak(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    @Embedded(prefix = "picture_") val picture: ModelMedia
)