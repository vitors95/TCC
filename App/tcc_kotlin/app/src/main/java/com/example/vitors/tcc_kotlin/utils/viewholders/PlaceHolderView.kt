package com.example.vitors.tcc_kotlin.utils.viewholders

import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.vitors.tcc_kotlin.models.Collect
import com.example.vitors.tcc_kotlin.models.Place
import com.example.vitors.tcc_kotlin.utils.helpers.DateHelper
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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

class PlaceHolderView(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val dateHelper = DateHelper()

    fun setup(place: Place, collects: Array<Collect>) {
        val entries: ArrayList<Entry> = arrayListOf()
        val timestamps: ArrayList<Long> = arrayListOf()

        collects.forEachIndexed { index, collect ->
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

        itemView.line_chart.data = lineData
        itemView.line_chart.description.text = ""
        itemView.line_chart.legend.isEnabled = true
        itemView.line_chart.invalidate()
        itemView.line_chart.axisRight.isEnabled = false
        itemView.line_chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        itemView.line_chart.axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        itemView.line_chart.axisLeft.setDrawGridLines(false)
        itemView.line_chart.axisRight.setDrawGridLines(false)
        itemView.line_chart.xAxis.setDrawGridLines(false)
        itemView.line_chart.extraBottomOffset = 25f

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

        itemView.text_equipment_description.text = place.equipment_description
        itemView.text_place_description.text = place.place_description
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