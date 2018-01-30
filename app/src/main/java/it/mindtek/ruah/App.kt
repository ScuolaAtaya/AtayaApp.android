package it.mindtek.ruah

import android.app.Application
import android.arch.persistence.room.Room
import android.support.multidex.MultiDexApplication
import it.mindtek.ruah.config.*
import it.mindtek.ruah.db.AppDatabase
import it.mindtek.ruah.kotlin.extensions.db
import it.mindtek.ruah.kotlin.extensions.ioThread

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        initRoom()
        initUnits()
//        initUnderstand()
//        initSpeak()
//        initRead()
//        initWrite()
    }

    private fun initRoom() {
        val room = Room.
                databaseBuilder(
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

    private fun initUnderstand(){
        if(db.understandDao().count() == 0){
            val understand = UnderstandGenerator.getUnderstand()
            val questions = UnderstandGenerator.getQuestions()
            val answers = UnderstandGenerator.getAnswers()

            ioThread {
                db.understandDao().saveCategories(understand)
                db.understandDao().saveQuestions(questions)
                db.understandDao().saveAnswers(answers)
            }
        }
    }

    private fun initSpeak(){
        if(db.speakDao().count() == 0){
            val speak = SpeakGenerator.getSpeaks()

            ioThread {
                db.speakDao().saveCategories(speak)
            }
        }
    }

    private fun initRead(){
        if(db.readDao().count() == 0){
            val read = ReadGenerator.getRead()
            val answers = ReadGenerator.getAnswers()

            ioThread {
                db.readDao().saveCategories(read)
                db.readDao().saveAnswers(answers)
            }
        }
    }

    private fun initWrite(){
        if(db.writeDao().count() == 0){
            val write = WriteGenerator.getWrites()

            ioThread {
                db.writeDao().saveCategories(write)
            }
        }
    }
}