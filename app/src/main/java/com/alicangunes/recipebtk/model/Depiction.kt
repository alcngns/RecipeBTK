package com.alicangunes.recipebtk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Depiction (

    @ColumnInfo("name")
    var name : String,

    @ColumnInfo("material")
    var material : String,

    @ColumnInfo("image")
    var image : ByteArray
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
