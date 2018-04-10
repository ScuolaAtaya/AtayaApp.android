package it.mindtek.ruah.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.db.models.ModelQuestion

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
class PojoQuestion {
    @Embedded
    var question: ModelQuestion? = null

    @Relation(parentColumn = "id", entityColumn = "question_id", entity = ModelAnswer::class)
    var answers: MutableList<ModelAnswer> = mutableListOf()
}