package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.mindtek.ruah.db.models.ModelSpeak

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Dao
interface DaoSpeak {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelSpeak>)

    @Query("SELECT * FROM speak WHERE unit_id = :unitId ")
    fun getSpeakByUnitId(unitId: Int): MutableList<ModelSpeak>
}