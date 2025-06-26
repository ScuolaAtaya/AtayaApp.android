package it.mindtek.ruah.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.IntArrayConverter
import it.mindtek.ruah.db.converters.MarkerArrayConverter
import it.mindtek.ruah.db.converters.StringArrayConverter
import it.mindtek.ruah.db.daos.*
import it.mindtek.ruah.db.models.*

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Database(
    entities = [
        ModelCategory::class,
        ModelUnit::class,
        ModelUnderstand::class,
        ModelQuestion::class,
        ModelAnswer::class,
        ModelSpeak::class,
        ModelRead::class,
        ModelReadOption::class,
        ModelWrite::class,
        ModelFinalTest::class,
        ModelFinalTestQuestion::class
    ], version = 35
)

@TypeConverters(StringArrayConverter::class, IntArrayConverter::class, MarkerArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): DaoCategory
    abstract fun unitDao(): DaoUnit
    abstract fun understandDao(): DaoUnderstand
    abstract fun speakDao(): DaoSpeak
    abstract fun readDao(): DaoRead
    abstract fun writeDao(): DaoWrite
    abstract fun finalTestDao(): DaoFinalTest

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun setInstance(instance: AppDatabase) {
            INSTANCE = instance
        }

        fun getInstance(): AppDatabase =
            INSTANCE ?: throw Exception("No room instance has been set!")
    }
}