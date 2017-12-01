package it.mindtek.ruah

import android.app.Application
import android.arch.persistence.room.Room
import it.mindtek.ruah.config.UnitGenerator
import it.mindtek.ruah.db.AppDatabase
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.ioThread

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initRoom()
        initUnits()
    }

    private fun initRoom() {
        val room = Room.
                databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        getString(R.string.database_name)
                )
                .allowMainThreadQueries()
                .build()
        AppDatabase.setInstance(room)
    }

    private fun initUnits() {
        println("HERE")
        if (db.unitDao().count() == 0) {
            val units = UnitGenerator.getUnits()
            try {
                ioThread {
                    db.unitDao().saveUnits(units)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}