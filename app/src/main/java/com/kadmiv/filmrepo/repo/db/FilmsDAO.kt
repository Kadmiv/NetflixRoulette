package com.kadmiv.filmrepo.repo.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.kadmiv.filmrepo.repo.db.models.FilmModel

@Dao
abstract class FilmsDAO {

    @Query("SELECT * FROM films_table")
    abstract fun getAll(): List<FilmModel>

    @Insert(onConflict = REPLACE)
    abstract fun insert(item: FilmModel)

    @Delete
    abstract fun delete(item: FilmModel)


}