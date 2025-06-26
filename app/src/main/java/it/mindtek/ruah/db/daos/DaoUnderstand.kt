package it.mindtek.ruah.db.daos

import androidx.room.*
import it.mindtek.ruah.db.models.*
import it.mindtek.ruah.pojos.PojoUnderstand

/**
 * Created by alessandrogaboardi on 06/12/2017.
 */
@Dao
interface DaoUnderstand {
    @Insert
    fun insertCategories(categories: MutableList<ModelUnderstand>)

    @Insert
    fun insertQuestions(questions: MutableList<ModelQuestion>)

    @Insert
    fun insertAnswers(answers: MutableList<ModelAnswer>)

    @Query("SELECT * FROM understand WHERE unit_id = :unitId")
    fun getUnderstandByUnitId(unitId: Int): MutableList<PojoUnderstand>

    @Query("SELECT COUNT(*) FROM understand WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int

    @Query("DELETE FROM understand")
    fun truncateCategories()

    @Query("DELETE FROM ModelQuestion")
    fun truncateQuestions()

    @Query("DELETE FROM ModelAnswer")
    fun truncateAnswers()

    @Transaction
    fun saveCategories(categories: MutableList<ModelUnderstand>) {
        truncateCategories()
        insertCategories(categories)
    }

    @Transaction
    fun saveQuestions(questions: MutableList<ModelQuestion>) {
        truncateQuestions()
        insertQuestions(questions)
    }

    @Transaction
    fun saveAnswers(answers: MutableList<ModelAnswer>) {
        truncateAnswers()
        insertAnswers(answers)
    }
}