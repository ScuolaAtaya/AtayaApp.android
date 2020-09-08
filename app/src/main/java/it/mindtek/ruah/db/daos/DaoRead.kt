package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadOption
import it.mindtek.ruah.pojos.PojoRead

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Dao
interface DaoRead {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelRead>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOptions(options: MutableList<ModelReadOption>)

    @Query("SELECT * FROM read WHERE unit_id = :unitId")
    fun getReadByUnitId(unitId: Int): MutableList<PojoRead>

    @Query("SELECT COUNT(*) FROM read WHERE unit_id = :unitId")
    fun countByUnitId(unitId: Int): Int
}