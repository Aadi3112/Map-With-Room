package com.example.mapwithtab.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mapwithtab.model.LocationTableModel

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertData(loginTableModel: LocationTableModel)

    @Query("SELECT * FROM LocationData")
    fun getLocationDetails(): LiveData<List<LocationTableModel>>


}