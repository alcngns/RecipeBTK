package com.alicangunes.recipebtk.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.alicangunes.recipebtk.model.Depiction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable


@Dao
interface DepictionDAO {

    @Query("SELECT * FROM Depiction")
    fun getAll() : Flowable<List<Depiction>>

    @Query("SELECT * FROM Depiction WHERE id = :id")
    fun findById(id : Int) : Flowable<Depiction>

    @Insert
    fun insert(depiction: Depiction) : Completable

    @Delete
    fun delete(depiction: Depiction) : Completable
}