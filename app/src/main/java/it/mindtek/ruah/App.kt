package it.mindtek.ruah

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import it.mindtek.ruah.config.ImageWithMarkersGenerator
import it.mindtek.ruah.config.UnitGenerator
import it.mindtek.ruah.db.AppDatabase
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.ioThread
import it.mindtek.ruah.ws.interfaces.ApiClient

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(applicationContext.getString(R.string.api_base_url), applicationContext.getString(R.string.api_key))
        ImageWithMarkersGenerator.init(applicationContext)
        initRoom()
        initUnits()
    }

    private fun initRoom() {
        val room = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                getString(R.string.database_name)
        )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        AppDatabase.setInstance(room)
    }

    private fun initUnits() {
        if (db.unitDao().count() == 0) {
            val units = UnitGenerator.getUnits()
            try {
                ioThread { db.unitDao().saveUnits(units) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}