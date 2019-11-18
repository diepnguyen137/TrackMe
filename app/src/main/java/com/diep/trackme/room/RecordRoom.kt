package com.diep.trackme.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.diep.trackme.model.Record

@Database(entities = [Record::class], version = 1)
abstract class RecordRoom : RoomDatabase(){
    abstract fun recordDao():RecordDao

    companion object{
        @Volatile
        private var INSTANCE: RecordRoom? = null

        fun getDatabase(context: Context): RecordRoom{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordRoom::class.java,
                    "record_db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}