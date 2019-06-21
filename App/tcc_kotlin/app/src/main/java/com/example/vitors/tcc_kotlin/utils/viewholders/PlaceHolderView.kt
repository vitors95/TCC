package com.example.vitors.tcc_kotlin.utils.viewholders

import android.graphics.Canvas
import android.graphics.Color
import android.opengl.Visibility
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.vitors.tcc_kotlin.models.Collect
import com.example.vitors.tcc_kotlin.models.Place
import com.example.vitors.tcc_kotlin.utils.enums.AccelerationAxis
import com.example.vitors.tcc_kotlin.utils.helpers.DateHelper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.android.synthetic.main.place_item.view.*
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList
import com.github.mikephil.charting.utils.EntryXComparator

class PlaceHolderView(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val dateHelper = DateHelper()
    private var selectedMode: Boolean = true // true for Dispersion mode and false for Time mode
    private var selectedAccelerationAxis: AccelerationAxis = AccelerationAxis.X_ACCELERATION_AXIS

    fun setup(place: Place, collects: Array<Collect>) {
        itemView.text_equipment_description.text = place.equipment_description
        itemView.text_place_description.text = place.place_description

        setupButtonListeners(collects)
        setupMode(collects)
    }

    private fun setupButtonListeners(collects: Array<Collect>) {
        itemView.tempButton.setOnClickListener {
            itemView.modeButton.visibility = View.INVISIBLE
            itemView.text_x_label.text = "Data"
            itemView.text_y_label.text = "Temperatura (°C)"
            setupTemp(collects)
        }
        itemView.xButton.setOnClickListener {
            itemView.modeButton.visibility = View.VISIBLE
            selectedAccelerationAxis = AccelerationAxis.X_ACCELERATION_AXIS
            setupMode(collects)
        }
        itemView.yButton.setOnClickListener {
            itemView.modeButton.visibility = View.VISIBLE
            selectedAccelerationAxis = AccelerationAxis.Y_ACCELERATION_AXIS
            setupMode(collects)
        }
        itemView.zButton.setOnClickListener {
            itemView.modeButton.visibility = View.VISIBLE
            selectedAccelerationAxis = AccelerationAxis.Z_ACCELERATION_AXIS
            setupMode(collects)
        }
        itemView.modeButton.setOnClickListener {
            selectedMode = !selectedMode
            setupMode(collects)
        }
    }

    private fun setupMode(collects: Array<Collect>) {
        if (selectedMode) {
            itemView.modeButton.text = "Dispersão"
            itemView.text_x_label.text = "RMS (g)"
            itemView.text_y_label.text = "Pico a Pico (g)"
            when(selectedAccelerationAxis) {
                AccelerationAxis.X_ACCELERATION_AXIS -> setupXAxis(collects)
                AccelerationAxis.Y_ACCELERATION_AXIS -> setupYAxis(collects)
                AccelerationAxis.Z_ACCELERATION_AXIS -> setupZAxis(collects)
            }
        }
        else {
            itemView.modeButton.text = "Tempo"
            itemView.text_x_label.text = "Data"
            itemView.text_y_label.text = "Pico a Pico (g)"
            when(selectedAccelerationAxis) {
                AccelerationAxis.X_ACCELERATION_AXIS -> setupXAxisInTime(collects)
                AccelerationAxis.Y_ACCELERATION_AXIS -> setupYAxisInTime(collects)
                AccelerationAxis.Z_ACCELERATION_AXIS -> setupZAxisInTime(collects)
            }
        }
    }

    private fun setupXAxis(collects: Array<Collect>) {
        resetLineChart()
        resetScatterChart()

        val entries: ArrayList<Entry> = arrayListOf()

        collects.forEach { collect ->
            entries.add(Entry(collect.rmsx.toFloat()/8192, collect.accx.toFloat()/8192))
        }

        Collections.sort(entries, EntryXComparator())

        setupScatterChart(entries)

    }

    private fun setupYAxis(collects: Array<Collect>) {
        resetLineChart()
        resetScatterChart()

        val entries: ArrayList<Entry> = arrayListOf()

        collects.forEach { collect ->
            entries.add(Entry(collect.rmsy.toFloat()/8192, collect.accy.toFloat()/8192))
        }

        Collections.sort(entries, EntryXComparator())

        setupScatterChart(entries)

    }

    private fun setupZAxis(collects: Array<Collect>) {
        resetLineChart()
        resetScatterChart()

        val entries: ArrayList<Entry> = arrayListOf()

        collects.forEach { collect ->
            entries.add(Entry(collect.rmsz.toFloat()/8192, collect.accz.toFloat()/8192))
        }

        Collections.sort(entries, EntryXComparator())

        setupScatterChart(entries)

    }

    private fun setupTemp(collects: Array<Collect>) {
        resetScatterChart()
        resetLineChart()

        val entries: ArrayList<Entry> = arrayListOf()
        val timestamps: ArrayList<Long> = arrayListOf()

        collects.forEachIndexed { index, collect ->
            entries.add(Entry(index.toFloat(), ((collect.temp/340)+36.53).toFloat()))
            timestamps.add(dateHelper.dateString2Timetamp(collect.data))
        }

        setupLineChart(entries, timestamps)
    }

    private fun setupXAxisInTime(collects: Array<Collect>) {
        resetScatterChart()
        resetLineChart()

        val entries: ArrayList<Entry> = arrayListOf()
        val timestamps: ArrayList<Long> = arrayListOf()

        collects.forEachIndexed { index, collect ->
            entries.add(Entry(index.toFloat(), collect.accx.toFloat()/8192))
            timestamps.add(dateHelper.dateString2Timetamp(collect.data))
        }

        setupLineChart(entries, timestamps)
    }

    private fun setupYAxisInTime(collects: Array<Collect>) {
        resetScatterChart()
        resetLineChart()

        val entries: ArrayList<Entry> = arrayListOf()
        val timestamps: ArrayList<Long> = arrayListOf()

        collects.forEachIndexed { index, collect ->
            entries.add(Entry(index.toFloat(), collect.accy.toFloat()/8192))
            timestamps.add(dateHelper.dateString2Timetamp(collect.data))
        }

        setupLineChart(entries, timestamps)
    }

    private fun setupZAxisInTime(collects: Array<Collect>) {
        resetScatterChart()
        resetLineChart()

        val entries: ArrayList<Entry> = arrayListOf()
        val timestamps: ArrayList<Long> = arrayListOf()

        collects.forEachIndexed { index, collect ->
            entries.add(Entry(index.toFloat(), collect.accz.toFloat()/8192))
            timestamps.add(dateHelper.dateString2Timetamp(collect.data))
        }

        setupLineChart(entries, timestamps)

    }

    private fun setupLineChart(entries: ArrayList<Entry>, timestamps: ArrayList<Long>) {
        val dataSet = LineDataSet(entries, "")
        dataSet.fillAlpha = 1100
        dataSet.color = Color.RED
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)
        val lineData = LineData(dataSet)

        itemView.line_chart.data = lineData
        itemView.line_chart.visibility = View.VISIBLE
        itemView.line_chart.description.text = ""
        itemView.line_chart.legend.isEnabled = false
        itemView.line_chart.invalidate()
        itemView.line_chart.axisRight.isEnabled = false
        itemView.line_chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        itemView.line_chart.axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        itemView.line_chart.axisLeft.granularity = 0.1f
        itemView.line_chart.axisLeft.isGranularityEnabled = true
        itemView.line_chart.axisLeft.setDrawGridLines(false)
        itemView.line_chart.axisRight.setDrawGridLines(false)
        itemView.line_chart.xAxis.setDrawGridLines(false)
        itemView.line_chart.extraBottomOffset = 20f

        val xAxis = itemView.line_chart.xAxis

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

        itemView.line_chart.setXAxisRenderer(
            CustomXAxisRenderer(
                itemView.line_chart.viewPortHandler,
                xAxis,
                itemView.line_chart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
    }

    private fun setupScatterChart(entries: ArrayList<Entry>) {
        val dataSet = ScatterDataSet(entries, "")
        dataSet.color = Color.RED
        dataSet.setDrawValues(false)
        val scatterData = ScatterData(dataSet)

        itemView.scatter_chart.visibility = View.VISIBLE
        itemView.scatter_chart.data = scatterData
        itemView.scatter_chart.description.text = ""
        itemView.scatter_chart.legend.isEnabled = false
        itemView.scatter_chart.invalidate()
        itemView.scatter_chart.axisRight.isEnabled = false
        itemView.scatter_chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        itemView.scatter_chart.axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        itemView.scatter_chart.axisLeft.axisMinimum = 0f
        itemView.scatter_chart.axisRight.axisMinimum = 0f
        itemView.scatter_chart.axisLeft.granularity = 0.1f
        itemView.scatter_chart.axisLeft.isGranularityEnabled = true
        itemView.scatter_chart.axisLeft.setDrawGridLines(false)
        itemView.scatter_chart.axisRight.setDrawGridLines(false)
        itemView.scatter_chart.xAxis.setDrawGridLines(false)
        itemView.scatter_chart.extraBottomOffset = 20f

        val xAxis = itemView.scatter_chart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.labelCount = 4
        xAxis.granularity = 0.1f
        xAxis.isGranularityEnabled = true

    }

    private fun resetLineChart() {
        itemView.line_chart.fitScreen()
        itemView.line_chart.data?.clearValues()
        itemView.line_chart.xAxis.valueFormatter = null
        itemView.line_chart.setXAxisRenderer(
            XAxisRenderer(
                itemView.line_chart.viewPortHandler,
                itemView.line_chart.xAxis,
                itemView.line_chart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        itemView.line_chart.notifyDataSetChanged()
        itemView.line_chart.clear()
        itemView.line_chart.invalidate()
        itemView.line_chart.visibility = View.INVISIBLE
    }

    private fun resetScatterChart() {
        itemView.scatter_chart.fitScreen()
        itemView.scatter_chart.data?.clearValues()
        itemView.scatter_chart.xAxis.valueFormatter = null
        itemView.scatter_chart.setXAxisRenderer(
            XAxisRenderer(
                itemView.scatter_chart.viewPortHandler,
                itemView.scatter_chart.xAxis,
                itemView.scatter_chart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        itemView.scatter_chart.notifyDataSetChanged()
        itemView.scatter_chart.clear()
        itemView.scatter_chart.invalidate()
        itemView.scatter_chart.visibility = View.INVISIBLE
    }

}

class DateFormatter(private val xValsDateLabel: ArrayList<String>): ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return value.toString()
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
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