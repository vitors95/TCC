package com.example.vitors.tcc_kotlin.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vitors.tcc_kotlin.Models.Equipment
import com.example.vitors.tcc_kotlin.R
import kotlinx.android.synthetic.main.place_item.view.*

class EquipmentAdapter(val equipments: Array<Equipment>): RecyclerView.Adapter<CustomHolderView>() {

    override fun getItemCount(): Int {
        return equipments.count()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomHolderView {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.place_item, p0, false)
        return CustomHolderView(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomHolderView, position: Int) {
        val equipment = equipments[position]
        holder.view.text_place_description.text = equipment.description

    }
}

class CustomHolderView(val view: View, var newEquipment: Equipment? = null): RecyclerView.ViewHolder(view) {

}