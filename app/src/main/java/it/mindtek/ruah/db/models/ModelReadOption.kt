package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "read_option")
open class ModelReadOption(
    @PrimaryKey val id: String,
     val read_id: String,
    val body: String,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    val markerId: String
)