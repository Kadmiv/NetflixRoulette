package com.kadmiv.filmrepo.repo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.kadmiv.filmrepo.repo.db.models.FilmModel

val DB_NAME = "app"

@Database(entities = [FilmModel::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    internal abstract fun dao(): FilmsDAO

    companion object {
        private var instance: AppDataBase? = null

        fun getInstance(context: Context?): AppDataBase {
            if (instance == null) {
                synchronized(AppDataBase::class) {
                    instance = Room.databaseBuilder(
                        context!!.applicationContext,
                        AppDataBase::class.java,
                        "$DB_NAME.db"
                    ).build()
                }
            }
            return instance!!
        }

        fun destroyInstance() {
            instance = null
        }
    }
}