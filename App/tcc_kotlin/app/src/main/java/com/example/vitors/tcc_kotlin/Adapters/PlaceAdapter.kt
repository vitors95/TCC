package com.example.vitors.tcc_kotlin.Adapters

import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vitors.tcc_kotlin.Models.Collect
import com.example.vitors.tcc_kotlin.Models.Place
import com.example.vitors.tcc_kotlin.R
import com.example.vitors.tcc_kotlin.Utils.DateHelper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.*
import kotlinx.android.synthetic.main.place_item.view.*
import okhttp3.internal.Util
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList

class PlaceAdapter(val places: Array<Place>, val collects: Array<Collect>): RecyclerView.Adapter<PlaceHolderView>() {

    val dateHelper = DateHelper()

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
        val timestamps: ArrayList<Long> = arrayListOf()

        collect.forEachIndexed { index, collect ->
            entries.add(Entry(index.toFloat(), ((collect.temp/340)+36.53).toFloat()))
            timestamps.add(dateHelper.dateString2Timetamp(collect.data))
        }

        val dataSet = LineDataSet(entries, "Temperatura (°C)")
        dataSet.fillAlpha = 1100
        dataSet.color = Color.RED
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        val lineData = LineData(dataSet)

        holder.view.line_chart.data = lineData
        holder.view.line_chart.description.text = ""
        holder.view.line_chart.legend.isEnabled = true
        holder.view.line_chart.invalidate()
        holder.view.line_chart.axisRight.isEnabled = false
        holder.view.line_chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        holder.view.line_chart.axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        holder.view.line_chart.axisLeft.setDrawGridLines(false)
        holder.view.line_chart.axisRight.setDrawGridLines(false)
        holder.view.line_chart.xAxis.setDrawGridLines(false)
        holder.view.line_chart.extraBottomOffset = 25f

        val xAxis = holder.view.line_chart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 4
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        val xValsDateLabel = ArrayList<String>()

        timestamps.forEach {
            val min = it / 60 % 60
            val hour = it / (60 * 60) % 24
            val day = dateHelper.timestamp2LocalDateTime(it).dayOfMonth
            val month = dateHelper.timestamp2LocalDateTime(it).month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("PT-BR"))
            val time = if (min > 9) "$hour:$min" else "$hour:0$min"
            val dateTime = if (day > 9) "$time\n$day/$month" else "$time\n0$day/$month"
            xValsDateLabel.add(dateTime)
        }

        xAxis.valueFormatter = (DateFormatter(xValsDateLabel))

        holder.view.line_chart.setXAxisRenderer(CustomXAxisRenderer(
            holder.view.line_chart.viewPortHandler,
            xAxis,
            holder.view.line_chart.getTransformer(YAxis.AxisDependency.LEFT)
        ))

        holder.view.text_equipment_description.text = place.equipment_description
        holder.view.text_place_description.text = place.place_description

    }

}

class PlaceHolderView(val view: View): RecyclerView.ViewHolder(view) {

}

class DateFormatter(private val xValsDateLabel: ArrayList<String>): ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if(value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
            return xValsDateLabel[value.toInt()]
        } else {
            return ("").toString()
        }
    }

}

class CustomXAxisRenderer(viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer): XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(c: Canvas?, formattedLabel: String?, x: Float, y: Float, anchor: MPPointF?, angleDegrees: Float) {
        formattedLabel?.let {
            val lines = it.split("\n")
            Utils.drawXAxisValue(c, lines[0], x, y, mAxisLabelPaint, anchor, angleDegrees)
            Utils.drawXAxisValue(c, lines[1], x, y + mAxisLabelPaint.textSize, mAxisLabelPaint, anchor, angleDegrees)
        }
    }
}