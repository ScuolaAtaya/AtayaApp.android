package it.mindtek.ruah.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadAnswer

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
class PojoRead {
    @Embedded
    var read: ModelRead? = null

    @Relation(parentColumn = "id", entityColumn = "read_id", entity = ModelReadAnswer::class)
    var answers: MutableList<ModelReadAnswer> = mutableListOf()

    val answersConverted: MutableList<ModelAnswer> get() {
        val ans = mutableListOf<ModelAnswer>()
        answers.forEach {
            val answ = ModelAnswer(
                    it.id,
                    "",
                    it.body,
                    it.audio,
                    it.correct
            )
            ans.add(answ)
        }
        return ans
    }
}