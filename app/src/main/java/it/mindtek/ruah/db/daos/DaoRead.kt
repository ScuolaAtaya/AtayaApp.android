package it.mindtek.ruah.db.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadAnswer
import it.mindtek.ruah.pojos.PojoRead

/**
 * Created by alessandrogaboardi on 20/12/2017.
 */
@Dao
interface DaoRead {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategories(categories: MutableList<ModelRead>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAnswers(answers: MutableList<ModelReadAnswer>)

    @Query("SELECT * FROM read")
    fun getReadAsync(): LiveData<MutableList<PojoRead>>

    @Query("SELECT * FROM read WHERE unit_id = :unit_id")
    fun getReadByUnitId(unit_id: Int): MutableList<PojoRead>

    @Query("SELECT COUNT(*) FROM read")
    fun count(): Int
}