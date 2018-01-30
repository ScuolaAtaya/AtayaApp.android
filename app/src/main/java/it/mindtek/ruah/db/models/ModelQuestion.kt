package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Entity
open class ModelQuestion(
        @PrimaryKey
        var id: Int = -1,
        var section_id: Int = -1,
        var body: String = "",
        var audio: String = ""
) {}