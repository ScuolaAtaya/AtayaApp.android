package it.mindtek.ruah.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import it.mindtek.ruah.db.models.ModelQuestion
import it.mindtek.ruah.db.models.ModelUnderstand

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
class UnderstandPojo {
    @Embedded
    var category: ModelUnderstand? = null

    @Relation(parentColumn = "id", entityColumn = "section_id", entity = ModelQuestion::class)
    var questions: MutableList<PojoQuestion> = mutableListOf()
}