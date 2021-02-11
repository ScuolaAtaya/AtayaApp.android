package it.mindtek.ruah.db.daos

import androidx.room.*
import it.mindtek.ruah.db.models.ModelSpeak

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Dao
interface DaoSpeak {
    @Insert
    fun insertAll(categories: MutableList<ModelSpeak>)

    @Query("SELECT * FROM speak WHERE unit_id = :unitId ")
    fun getSpeakByUnitId(unitId: Int): MutableList<ModelSpeak>

    @Query("SELECT COUNT(*) FROM speak WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int

    @Query("DELETE FROM speak")
    fun truncate()

    @Transaction
    fun saveCategories(categories: MutableList<ModelSpeak>) {
        truncate()
        insertAll(categories)
    }
}