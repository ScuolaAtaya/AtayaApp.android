package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Entity(tableName = "understand")
open class ModelUnderstand(
        @PrimaryKey
        var id: Int = 0,
        var unit_id: Int = 0,
        var video_url: String = "",
        var audio: String = ""
) {}