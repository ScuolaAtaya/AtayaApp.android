package it.mindtek.ruah.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import it.mindtek.ruah.db.models.ModelUnit

@Dao
interface DaoUnit {
    @Insert
    fun saveUnits(units: MutableList<ModelUnit>)

    @Update
    fun updateUnit(unit: ModelUnit)

    @Query("SELECT * FROM units")
    fun getUnitsAsync(): LiveData<MutableList<ModelUnit>>

    @Query("SELECT * FROM units WHERE category_id = :categoryId")
    fun getUnitsByCategoryIdAsync(categoryId: Int): LiveData<MutableList<ModelUnit>>

    @Query("SELECT * FROM units WHERE id = :unitId LIMIT 1")
    fun getUnitByIdAsync(unitId: Int): LiveData<ModelUnit>

    @Query("SELECT * FROM units WHERE id = :unitId LIMIT 1")
    fun getUnitById(unitId: Int): ModelUnit?

    @Query("SELECT COUNT(*) FROM units")
    fun count(): Int
}