package it.mindtek.ruah.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import it.mindtek.ruah.db.daos.DaoUnderstand
import it.mindtek.ruah.db.daos.DaoUnit
import it.mindtek.ruah.db.models.ModelAnswer
import it.mindtek.ruah.db.models.ModelQuestion
import it.mindtek.ruah.db.models.ModelUnderstand
import it.mindtek.ruah.db.models.ModelUnit

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Database(version = 2, entities = [ModelUnit::class, ModelUnderstand::class, ModelQuestion::class, ModelAnswer::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun unitDao(): DaoUnit
    abstract fun understandDao(): DaoUnderstand

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun setInstance(instance: AppDatabase) {
            INSTANCE = instance
        }

        fun getInstance(): AppDatabase{
            INSTANCE?.let {
                return it
            } ?: throw Exception("No room instance has been set!!")
        }
    }
}