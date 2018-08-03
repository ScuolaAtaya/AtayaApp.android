package it.mindtek.ruah.db.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import it.mindtek.ruah.db.models.ModelSpeak

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
@Dao
interface DaoSpeak {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelSpeak>)

    @Query("SELECT * FROM speak")
    fun getSpeakAsync(): LiveData<MutableList<ModelSpeak>>

    @Query("SELECT * FROM speak WHERE unit_id = :unit_id ")
    fun getSpeakByUnitId(unit_id: Int): MutableList<ModelSpeak>

    @Query("SELECT COUNT(*) FROM speak")
    fun count(): Int
}