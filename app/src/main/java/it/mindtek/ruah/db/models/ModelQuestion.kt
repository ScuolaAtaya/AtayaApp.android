package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Entity
open class ModelQuestion(
        @PrimaryKey
        var id: String = "",
        var section_id: String = "",
        var body: String = "",
        @Embedded(prefix = "audio_")
        var audio: ModelMedia,
        @Embedded(prefix = "picture_")
        var picture: ModelMedia
)