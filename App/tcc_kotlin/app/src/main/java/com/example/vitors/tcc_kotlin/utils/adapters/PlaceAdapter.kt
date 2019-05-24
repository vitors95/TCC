package com.example.vitors.tcc_kotlin.utils.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.vitors.tcc_kotlin.models.Collect
import com.example.vitors.tcc_kotlin.models.Place
import com.example.vitors.tcc_kotlin.R
import com.example.vitors.tcc_kotlin.utils.viewholders.PlaceHolderView

class PlaceAdapter(val places: Array<Place>, val collects: Array<Collect>): RecyclerView.Adapter<PlaceHolderView>() {

    override fun getItemCount(): Int {
        return places.count()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PlaceHolderView {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.place_item, p0, false)
        return PlaceHolderView(view)
    }

    override fun onBindViewHolder(viewHolder: PlaceHolderView, position: Int) {
        viewHolder.setup(places[position], collects)
    }

}