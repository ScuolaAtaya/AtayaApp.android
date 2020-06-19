package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "read_answer")
open class ModelReadAnswer(
        @PrimaryKey
        var id: String = "",
        var read_id: String = "",
        var body: String = "",
        @Embedded(prefix = "audio_")
        var audio: ModelMedia,
        var correct: Boolean = false
)