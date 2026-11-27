package com.example.androidwithkotlin.AndroidWithKotlin.Roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class PesonDatabase : RoomDatabase() {

    abstract fun personDao(): PersonDao

    companion object {

        @Volatile
        private var INSTANCE: PesonDatabase? = null
        fun getDatabase(context: Context): PesonDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PesonDatabase::class.java,
                    "person_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}