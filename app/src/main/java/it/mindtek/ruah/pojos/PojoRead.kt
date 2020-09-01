package it.mindtek.ruah.pojos

import androidx.room.Embedded
import androidx.room.Relation
import it.mindtek.ruah.db.models.ModelMarker
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadOption

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
class PojoRead {
    @Embedded
    var read: ModelRead? = null

    @Relation(parentColumn = "id", entityColumn = "section_id", entity = ModelReadOption::class)
    var options: MutableList<ModelReadOption> = mutableListOf()

    @Relation(parentColumn = "id", entityColumn = "section_id", entity = ModelMarker::class)
    var markers: MutableList<ModelMarker> = mutableListOf()
}