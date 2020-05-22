package it.mindtek.ruah.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.mindtek.ruah.db.models.ModelUnit

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Dao
interface DaoUnit {
    @Insert
    fun saveUnits(units: MutableList<ModelUnit>)

    @Update
    fun updateUnit(unit: ModelUnit)

    @Query("SELECT * FROM units")
    fun getUnitsAsync(): LiveData<MutableList<ModelUnit>>

    @Query("SELECT * FROM units WHERE id = :unit_id LIMIT 1 ")
    fun getUnitByIdAsync(unit_id: Int): LiveData<ModelUnit>

    @Query("SELECT * FROM units WHERE id = :unit_id LIMIT 1 ")
    fun getUnitById(unit_id: Int): ModelUnit?

    @Query("SELECT COUNT(*) FROM units")
    fun count(): Int
}