package it.mindtek.ruah.db.daos

import androidx.room.*
import it.mindtek.ruah.db.models.ModelWrite

/**
 * Created by alessandrogaboardi on 22/12/2017.
 */
@Dao
interface DaoWrite {
    @Insert
    fun insertAll(categories: MutableList<ModelWrite>)

    @Query("SELECT * FROM write WHERE unit_id = :unitId")
    fun getWriteByUnitId(unitId: Int): MutableList<ModelWrite>

    @Query("SELECT COUNT(*) FROM write WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int

    @Query("DELETE FROM write")
    fun truncate()

    @Transaction
    fun saveCategories(categories: MutableList<ModelWrite>) {
        truncate()
        insertAll(categories)
    }
}