package it.mindtek.ruah

import androidx.multidex.MultiDexApplication
import androidx.room.Room
import com.bugsee.library.Bugsee
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
        // initBugsee()
        ApiClient.init(applicationContext.getString(R.string.api_base_url), applicationContext.getString(R.string.api_key))
        initRoom()
        initUnits()
    }

    private fun initBugsee() {
        val options = HashMap<String, Any>()
        options[Bugsee.Option.NotificationBarTrigger] = false
        options[Bugsee.Option.ShakeToTrigger] = false
        Bugsee.launch(this, "674ab8be-e65a-46f8-aec7-da49e35e8c62", options)
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
                ioThread {
                    db.unitDao().saveUnits(units)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}