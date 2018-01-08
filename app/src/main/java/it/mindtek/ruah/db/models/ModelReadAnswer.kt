package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "readanswer")
open class ModelReadAnswer(
        @PrimaryKey
        var id: Int = 0,
        var read_id: Int = 0,
        var body: String = "",
        var audio: String = "",
        var correct: Boolean = false
) {
}