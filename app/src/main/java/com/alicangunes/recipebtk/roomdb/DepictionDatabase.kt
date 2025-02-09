package com.alicangunes.recipebtk.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alicangunes.recipebtk.model.Depiction


@Database(entities = [Depiction::class], version = 1)
abstract class DepictionDatabase : RoomDatabase() {
    abstract fun DepictionDao(): DepictionDAO
}