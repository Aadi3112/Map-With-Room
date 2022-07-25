package com.example.mapwithtab.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.mapwithtab.model.LocationTableModel
import com.example.mapwithtab.room.LocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class LocationRepository {

    companion object {

        var locationDatabase: LocationDatabase? = null

        var locationTableModel: LiveData<List<LocationTableModel>>? = null

        fun initializeDB(context: Context): LocationDatabase {
            return LocationDatabase.getDataseClient(context)
        }

        suspend fun insertData(
            context: Context,
            name: String,
            email: String,
            phone: String,
            latlang: String,
            distance: String
        ) {

            locationDatabase = initializeDB(context)


            val locationDetails = LocationTableModel(name, email, phone, latlang, distance)
            locationDatabase!!.locationDao().InsertData(locationDetails)


        }

        fun getLoginDetails(context: Context): LiveData<List<LocationTableModel>>? {

            locationDatabase = initializeDB(context)

            locationTableModel = locationDatabase!!.locationDao().getLocationDetails()

            return locationTableModel
        }

    }
}