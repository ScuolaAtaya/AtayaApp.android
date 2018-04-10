package it.mindtek.ruah.db.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.db.models.ModelQuestion
import it.mindtek.ruah.db.models.ModelUnderstand
import it.mindtek.ruah.pojos.UnderstandPojo

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Dao
interface DaoUnderstand {
    @Insert
    fun saveCategories(categories: MutableList<ModelUnderstand>)

    @Insert
    fun saveQuestions(questions: MutableList<ModelQuestion>)

    @Insert
    fun saveAnswers(answers: MutableList<ModelAnswer>)

    @Query("SELECT * FROM understand")
    fun getUnderstandAsync(): LiveData<MutableList<UnderstandPojo>>

    @Query("SELECT * FROM understand WHERE unit_id = :unit_id LIMIT 1 ")
    fun getUnderstandByUnitId(unit_id: Int): UnderstandPojo?

    @Query("SELECT COUNT(*) FROM understand")
    fun count(): Int
}