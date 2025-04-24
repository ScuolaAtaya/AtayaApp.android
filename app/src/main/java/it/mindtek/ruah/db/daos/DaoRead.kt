package it.mindtek.ruah.db.daos

import androidx.room.*
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.pojos.PojoRead

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Dao
interface DaoRead {
    @Insert
    fun insertCategories(categories: MutableList<ModelRead>)

    @Insert
    fun insertOptions(options: MutableList<ModelReadOption>)

    @Transaction
    @Query("SELECT * FROM read WHERE unit_id = :unitId")
    fun getReadByUnitId(unitId: Int): MutableList<PojoRead>

    @Query("SELECT COUNT(*) FROM read WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int

    @Query("DELETE FROM read")
    fun truncateCategories()

    @Query("DELETE FROM read_option")
    fun truncateOptions()

    @Transaction
    fun saveCategories(categories: MutableList<ModelRead>) {
        truncateCategories()
        insertCategories(categories)
    }

    @Transaction
    fun saveOptions(options: MutableList<ModelReadOption>) {
        truncateOptions()
        insertOptions(options)
    }
}