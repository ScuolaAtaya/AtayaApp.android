package it.mindtek.ruah.db.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import it.mindtek.ruah.db.models.ModelUnit

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Dao
open interface DaoUnit {
    @Insert
    fun saveUnits(units: MutableList<ModelUnit>)

    @Query("SELECT * FROM units")
    fun getUnitsAsync(): LiveData<MutableList<ModelUnit>>

    @Query("SELECT * FROM units WHERE id = :unit_id LIMIT 1 ")
    fun getUnitByIdAsync(unit_id: Int): LiveData<ModelUnit>

    @Query("SELECT COUNT(*) FROM units")
    fun count(): Int
}