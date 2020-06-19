package it.mindtek.ruah.pojos

import androidx.room.Embedded
import androidx.room.Relation
import it.mindtek.ruah.db.models.ModelFinalTest
import it.mindtek.ruah.db.models.ModelFinalTestQuestion

class PojoFinalTest {
    @Embedded
    var finalTest: ModelFinalTest? = null

    @Relation(parentColumn = "id", entityColumn = "section_id", entity = ModelFinalTestQuestion::class)
    var questions: MutableList<ModelFinalTestQuestion> = mutableListOf()
}