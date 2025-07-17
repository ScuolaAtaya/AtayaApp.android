package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "final_question")
open class ModelFinalTestQuestion(
    @PrimaryKey val id: String,
    val section_id: String,
    val body: String,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    @Embedded(prefix = "picture_") val picture: ModelMedia?,
    val answers: Boolean
)