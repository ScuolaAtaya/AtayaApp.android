package it.mindtek.ruah.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single
import it.mindtek.ruah.db.models.ModelCategory

@Dao
interface DaoCategory {
    @Insert
    fun saveCategories(units: MutableList<ModelCategory>)

    @Query("SELECT * FROM category")
    fun getCategoriesAsync(): Single<MutableList<ModelCategory>>

    @Query("SELECT COUNT(*) FROM category")
    fun count(): Int
}