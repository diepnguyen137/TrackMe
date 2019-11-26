package com.diep.trackme.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.diep.trackme.model.Record

@Dao
interface RecordDao {
    @Insert
    fun insertRecord(vararg record: Record)

    @Query("SELECT * FROM record_db")
    fun getRecordList():LiveData<List<Record>>
}