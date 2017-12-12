package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Entity
open class ModelAnswer(
        @PrimaryKey
        var id: Int = 0,
        var question_id: Int = 0,
        var body: String = "",
        var audio: String = "",
        var correct: Boolean = false
) {}