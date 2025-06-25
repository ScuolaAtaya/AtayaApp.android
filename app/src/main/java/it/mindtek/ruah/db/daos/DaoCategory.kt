package it.mindtek.ruah.db.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import it.mindtek.ruah.db.models.ModelCategory

@Dao
interface DaoCategory {
    @Insert
    fun saveCategories(units: MutableList<ModelCategory>)

    @Query("SELECT * FROM category")
    fun getCategoriesAsync(): LiveData<MutableList<ModelCategory>>

    @Query("SELECT COUNT(*) FROM category")
    fun count(): Int
}