package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "read")
open class ModelRead(
        @PrimaryKey
        var id: Int = 0,
        var unit_id: Int = 0,
        var picture: String = ""
) {
}