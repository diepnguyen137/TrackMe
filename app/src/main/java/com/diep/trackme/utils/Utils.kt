package com.diep.trackme.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.TextView
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun TextView.setDistanceText(value: String){
    this.text = "$value m"
}
fun TextView.setSpeedText(value: String){
    this.text = "$value km/h"
}
fun Chronometer.formatTimeDisplay(){
    val text = this.text
    if(text.length == 5) {
        this.text = "00:$text"
    } else if(text.length == 7){
        this.text = "0$text"
    }
}
