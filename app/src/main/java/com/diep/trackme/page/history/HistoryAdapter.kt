package com.diep.trackme.page.history

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diep.trackme.R
import com.diep.trackme.model.Record
import com.diep.trackme.utils.inflate
import com.diep.trackme.utils.setDistanceText
import com.diep.trackme.utils.setSpeedText
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter(context: Context): RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    private var recordList: ArrayList<Record> = arrayListOf()
    private var context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.inflate(R.layout.history_item, false)
        return ViewHolder(inflater)
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recordList[position])

    }
    fun setAdapter (value: ArrayList<Record>){
        recordList = value
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), OnMapReadyCallback{
        private lateinit var map:GoogleMap
        private lateinit var startLatLng: LatLng
        private lateinit var stopLatLng: LatLng
        init {
            with(itemView.map_view){
                onCreate(null)
                getMapAsync(this@ViewHolder)
            }
        }
        override fun onMapReady(map: GoogleMap?) {
            MapsInitializer.initialize(context)
            this.map = map ?: return
            setUpMap()
        }

        private fun setUpMap() {
            if (!::map.isInitialized) return
            with(map){
                moveCamera(CameraUpdateFactory.newLatLngZoom(stopLatLng,13f))
                addMarker(MarkerOptions().position(startLatLng))
                addMarker(MarkerOptions().position(stopLatLng))
                val line = addPolyline(PolylineOptions().add(startLatLng,stopLatLng))
                line.color = Color.RED
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
        private fun clearView(){
            with(map){
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
            }
        }
        fun bind(item: Record){
            itemView.distance_tv.setDistanceText(item.distance)
            itemView.speed_tv.setSpeedText(item.averageSpeed)
            itemView.duration_tv.text = item.durationVal
            startLatLng = LatLng(item.startLat, item.startLng)
            stopLatLng = LatLng(item.stopLat, item.stopLng)
            setUpMap()
        }
    }
}