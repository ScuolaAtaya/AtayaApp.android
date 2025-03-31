package it.mindtek.ruah

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import it.mindtek.ruah.config.ImageWithMarkersGenerator
import it.mindtek.ruah.config.UnitGenerator
import it.mindtek.ruah.db.AppDatabase
import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.ioThread
import it.mindtek.ruah.ws.interfaces.ApiClient

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(
            applicationContext.getString(R.string.api_base_url),
            applicationContext.getString(R.string.api_key)
        )
        ImageWithMarkersGenerator.init(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(
                getString(R.string.notification_channel),
                getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        AppDatabase.setInstance(
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                getString(R.string.database_name)
            ).fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        )
        if (db.unitDao().count() == 0) {
            val units: MutableList<ModelUnit> = UnitGenerator.getUnits()
            try {
                ioThread { db.unitDao().saveUnits(units) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val APP_SP = "app"
    }
}