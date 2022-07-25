package com.example.mapwithtab.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapwithtab.model.LocationTableModel

@Database(entities = arrayOf(LocationTableModel::class), version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao() : DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDataseClient(context: Context) : LocationDatabase {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {

                INSTANCE = Room
                    .databaseBuilder(context, LocationDatabase::class.java, "LOCATION")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}