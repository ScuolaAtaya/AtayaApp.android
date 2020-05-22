package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "readanswer")
open class ModelReadAnswer(
        @PrimaryKey
        var id: String = "",
        var read_id: String = "",
        var body: String = "",
        var audio: String = "",
        var correct: Boolean = false
) {
}