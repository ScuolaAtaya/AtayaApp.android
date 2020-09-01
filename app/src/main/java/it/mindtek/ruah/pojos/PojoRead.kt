package it.mindtek.ruah.pojos

import androidx.room.Embedded
import androidx.room.Relation
import it.mindtek.ruah.db.models.ModelMarker
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadAnswer

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
class PojoRead {
    @Embedded
    var read: ModelRead? = null

    @Relation(parentColumn = "id", entityColumn = "section_id", entity = ModelReadAnswer::class)
    var options: MutableList<ModelReadAnswer> = mutableListOf()

    @Relation(parentColumn = "id", entityColumn = "section_id", entity = ModelMarker::class)
    var markers: MutableList<ModelReadAnswer> = mutableListOf()
}