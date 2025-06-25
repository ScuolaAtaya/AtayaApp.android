package it.mindtek.ruah.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "final_question")
open class ModelFinalTestQuestion(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "section_id") val sectionId: String,
    val body: String,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    @Embedded(prefix = "picture_") val picture: ModelMedia?,
    val answers: Boolean
)