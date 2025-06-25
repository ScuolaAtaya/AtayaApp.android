package it.mindtek.ruah.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Entity
open class ModelAnswer(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "question_id") val questionId: String,
    val body: String,
    @Embedded(prefix = "audio_") val audio: ModelMedia,
    val correct: Boolean
)