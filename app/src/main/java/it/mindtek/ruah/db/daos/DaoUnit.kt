package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single
import it.mindtek.ruah.db.models.ModelUnit

@Dao
interface DaoUnit {
    @Insert
    fun saveUnits(units: MutableList<ModelUnit>)

    @Update
    fun updateUnit(unit: ModelUnit)

    @Query("SELECT * FROM units")
    fun getUnitsAsync(): Single<MutableList<ModelUnit>>

    @Query("SELECT * FROM units WHERE categoryId = :categoryId")
    fun getUnitsByCategoryIdAsync(categoryId: Int): Single<MutableList<ModelUnit>>

    @Query("SELECT * FROM units WHERE id = :unitId LIMIT 1")
    fun getUnitByIdAsync(unitId: Int): Single<ModelUnit>

    @Query("SELECT * FROM units WHERE id = :unitId LIMIT 1")
    fun getUnitById(unitId: Int): ModelUnit?

    @Query("SELECT COUNT(*) FROM units")
    fun count(): Int
}