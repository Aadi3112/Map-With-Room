package com.example.mapwithtab.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationData")
data class LocationTableModel(

    @ColumnInfo(name = "name")
    var Name: String,

    @ColumnInfo(name = "email")
    var Email: String,

    @ColumnInfo(name = "phone")
    var Phone: String,

    @ColumnInfo(name = "latlang")
    var LatLand: String,

    @ColumnInfo(name = "distance")
    var Distance: String,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}
