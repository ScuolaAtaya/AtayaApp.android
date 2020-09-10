package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.db.models.ModelQuestion
import it.mindtek.ruah.db.models.ModelUnderstand
import it.mindtek.ruah.pojos.PojoUnderstand

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Dao
interface DaoUnderstand {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelUnderstand>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveQuestions(questions: MutableList<ModelQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAnswers(answers: MutableList<ModelAnswer>)

    @Query("SELECT * FROM understand WHERE unit_id = :unitId")
    fun getUnderstandByUnitId(unitId: Int): MutableList<PojoUnderstand>

    @Query("SELECT COUNT(*) FROM understand WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int
}