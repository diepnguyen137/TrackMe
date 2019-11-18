package com.diep.trackme.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_db")
data class Record(
    var distance: String,
    var durationVal: String,
    var averageSpeed: String,
    var startLat: Double,
    var startLng: Double,
    var stopLat: Double,
    var stopLng: Double
){
    @PrimaryKey(autoGenerate = true)
    var recordID: Int = 0
}

