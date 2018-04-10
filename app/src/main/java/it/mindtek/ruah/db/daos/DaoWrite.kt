package it.mindtek.ruah.db.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import it.mindtek.ruah.db.models.ModelWrite

/**
 * Created by alessandrogaboardi on 22/12/2017.
 */
@Dao
interface DaoWrite {
    @Insert
    fun saveCategories(categories: MutableList<ModelWrite>)

    @Query("SELECT * FROM write")
    fun getWriteAsync(): LiveData<MutableList<ModelWrite>>

    @Query("SELECT * FROM write WHERE unit_id = :unit_id")
    fun getWriteByUnitId(unit_id: Int): MutableList<ModelWrite>

    @Query("SELECT COUNT(*) FROM write")
    fun count(): Int
}