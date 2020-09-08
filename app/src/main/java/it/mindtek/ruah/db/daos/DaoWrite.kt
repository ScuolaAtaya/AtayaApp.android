package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.mindtek.ruah.db.models.ModelWrite

/**
 * Created by alessandrogaboardi on 22/12/2017.
 */
@Dao
interface DaoWrite {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelWrite>)

    @Query("SELECT * FROM write WHERE unit_id = :unitId")
    fun getWriteByUnitId(unitId: Int): MutableList<ModelWrite>
}