package it.mindtek.ruah.db.daos

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM speak")
    fun getSpeakAsync(): LiveData<MutableList<ModelSpeak>>

    @Query("SELECT * FROM speak WHERE unit_id = :unit_id ")
    fun getSpeakByUnitId(unit_id: Int): MutableList<ModelSpeak>

    @Query("SELECT COUNT(*) FROM speak")
    fun count(): Int
}