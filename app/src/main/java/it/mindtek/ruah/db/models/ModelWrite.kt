package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.StringArrayConverter

/**
 * Created by alessandrogaboardi on 21/12/2017.
 */
@Entity(tableName = "write")
open class ModelWrite(
    @PrimaryKey val id: String,
    val unit_id: Int,
    val word: String,
    val type: String,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    @Embedded(prefix = "picture_") val picture: ModelMedia,
    @param:TypeConverters(StringArrayConverter::class) var letters: MutableList<ModelSyllable> = mutableListOf()
)