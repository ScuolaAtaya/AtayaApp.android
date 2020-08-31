package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "final_question")
open class ModelFinalTestQuestion (
        @PrimaryKey
        var id: String = "",
        var section_id: String = "",
        var body: String = "",
        @Embedded(prefix = "audio_")
        var audio: ModelMedia,
        @Embedded(prefix = "picture_")
        var picture: ModelMedia?,
        var answers: Boolean = false
)