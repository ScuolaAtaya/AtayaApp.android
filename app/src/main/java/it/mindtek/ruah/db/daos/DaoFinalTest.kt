package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.mindtek.ruah.db.models.ModelFinalTest
import it.mindtek.ruah.db.models.ModelFinalTestQuestion
import it.mindtek.ruah.pojos.PojoFinalTest

@Dao
interface DaoFinalTest {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelFinalTest>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveQuestions(questions: MutableList<ModelFinalTestQuestion>)

    @Query("SELECT * FROM final WHERE unit_id = :unitId")
    fun getFinalTestByUnitId(unitId: Int): MutableList<PojoFinalTest>
}