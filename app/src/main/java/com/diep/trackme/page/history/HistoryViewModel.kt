package com.diep.trackme.page.history

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diep.trackme.model.Record
import com.diep.trackme.room.RecordDao
import com.diep.trackme.room.RecordRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(context: Context) : ViewModel() {
    private var dao: RecordDao = RecordRoom.getDatabase(context).recordDao()
    //Signal
    var recordList: LiveData<List<Record>>? = null

    //Trigger
    fun triggerSaveRecord(record: Record) = viewModelScope.launch (Dispatchers.IO){
        saveRecord(record)
    }

    fun getListRecord(){
        recordList =  dao.getRecordList()
    }

    @WorkerThread
    suspend fun saveRecord(record:Record){
        dao.insertRecord(record)
    }
}