package com.example.vitors.tcc_kotlin.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vitors.tcc_kotlin.Models.Collect
import com.example.vitors.tcc_kotlin.Models.Place
import com.example.vitors.tcc_kotlin.R
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.place_item.view.*

class PlaceAdapter(val places: Array<Place>, val collects: Array<Collect>): RecyclerView.Adapter<PlaceHolderView>() {

    override fun getItemCount(): Int {
        return places.count()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PlaceHolderView {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.place_item, p0, false)
        return PlaceHolderView(cellForRow)
    }

    override fun onBindViewHolder(holder: PlaceHolderView, position: Int) {
        val place = places[position]
        val collect = collects
        val entries: ArrayList<Entry> = arrayListOf()

        collect.forEach { entries.add(Entry(1.toFloat(), it.temp.toFloat())) }

        val dataSet = LineDataSet(entries, "Temperatura")
        val lineData = LineData(dataSet)

        holder.view.line_chart.data = lineData
        holder.view.text_equipment_description.text = place.equipment_description
        holder.view.text_place_description.text = place.place_description

    }
}

class PlaceHolderView(val view: View): RecyclerView.ViewHolder(view) {

}