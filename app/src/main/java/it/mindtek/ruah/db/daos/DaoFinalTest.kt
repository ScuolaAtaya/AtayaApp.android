package it.mindtek.ruah.db.daos

import androidx.room.*
import it.mindtek.ruah.db.models.ModelFinalTest
import it.mindtek.ruah.db.models.ModelFinalTestQuestion
import it.mindtek.ruah.pojos.PojoFinalTest

@Dao
interface DaoFinalTest {
    @Insert
    fun insertCategories(categories: MutableList<ModelFinalTest>)

    @Insert
    fun insertQuestions(questions: MutableList<ModelFinalTestQuestion>)

    @Query("SELECT * FROM final WHERE unit_id = :unitId")
    fun getFinalTestByUnitId(unitId: Int): MutableList<PojoFinalTest>

    @Query("SELECT COUNT(*) FROM final WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int

    @Query("DELETE FROM final")
    fun truncateCategories()

    @Query("DELETE FROM final_question")
    fun truncateQuestions()

    @Transaction
    fun saveCategories(categories: MutableList<ModelFinalTest>) {
        truncateCategories()
        insertCategories(categories)
    }

    @Transaction
    fun saveQuestions(questions: MutableList<ModelFinalTestQuestion>) {
        truncateQuestions()
        insertQuestions(questions)
    }
}