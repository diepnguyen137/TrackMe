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
    private val localRecordList = arrayListOf<Record>()
    //Signal
    val recordList: LiveData<ArrayList<Record>>
        get() = _recordList
    //Subject
    private val _recordList by lazy { MutableLiveData<ArrayList<Record>>() }

    init {
        getListRecord()
    }

    //Trigger
    fun triggerSaveRecord(record: Record) = viewModelScope.launch (Dispatchers.IO){
        saveRecord(record)
    }

    private fun getListRecord() {
        if(dao.getRecordList().value != null){
            localRecordList.addAll(dao.getRecordList().value!!)
        }
        _recordList.value = localRecordList
    }

    @WorkerThread
    suspend fun saveRecord(record:Record){
        dao.saveRecord(record)
    }


}