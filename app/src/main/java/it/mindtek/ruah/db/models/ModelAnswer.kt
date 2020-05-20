package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Entity
open class ModelAnswer(
        @PrimaryKey
        var id: String = "",
        var question_id: String = "",
        var body: String = "",
        var audio: String = "",
        var correct: Boolean = false
) {}