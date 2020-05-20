package it.mindtek.ruah.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Entity(tableName = "read")
open class ModelRead(
        @PrimaryKey
        var id: String = "",
        var unit_id: Int = 0,
        var picture: String = ""
) {
}