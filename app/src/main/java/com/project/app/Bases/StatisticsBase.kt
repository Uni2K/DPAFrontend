package com.project.app.Bases

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.project.app.CustomViews.CustomLineChartRenderer
import com.project.app.CustomViews.CustomMarkerView
import com.project.app.R
import kotlin.math.roundToInt

class StatisticsBase {


    fun setUpDefaultLineChart(mainChart: LineChart, axisColor: Int) {
        DesignBase.removeChartLabels(mainChart)
        mainChart.xAxis.isEnabled = false
        mainChart.axisLeft.isEnabled = true
        mainChart.axisRight.isEnabled = false
        mainChart.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        mainChart.axisLeft.granularity=10f
       // mainChart.axisLeft.setAxisMaximum(100f)
        mainChart.axisLeft.mAxisMinimum=0f

        mainChart.axisLeft.textColor=axisColor
        val renderer = CustomLineChartRenderer(
            mainChart,
            mainChart.animator,
            mainChart.viewPortHandler
        )
        renderer.hideFirstAndLast = true
        mainChart.renderer = renderer
        mainChart.isHighlightPerTapEnabled = false
        mainChart.setViewPortOffsets(0f, 0f, 0f, 0f)
        val xAxis = mainChart.xAxis
        xAxis.textColor = axisColor
        xAxis.isEnabled = true
        xAxis.valueFormatter = object : ValueFormatter() {


            override fun getFormattedValue(value: Float): String {
                if (value == -1f || value == 4f) return ""



                return "1 day ago"
            }


        }
       mainChart.axisLeft.valueFormatter = object : ValueFormatter() {


            override fun getFormattedValue(value: Float): String {
                if(value==0f)return ""
                return " ${value.roundToInt() }%"
            }


        }



        val marker = CustomMarkerView(mainChart.context, R.layout.marker_account)
        mainChart.marker = marker
        mainChart.setDrawMarkers(true)
        xAxis.granularity = 1f

    }

    fun setUpDefaultLineData(
        lineDataset: LineDataSet,
        content: ArrayList<Entry>,
        color: Int,
        dotColor: Int,
        dotColorHole: Int
    ) {

        var dotColors = ArrayList<Int>()
        var holeColors = ArrayList<Int>()

        while (dotColors.size < content.size) {
            dotColors.add(dotColor)
        }
        while (holeColors.size < content.size) {
            holeColors.add(dotColorHole)
        }


        DesignBase.setUpLineDataSet(
            lineDataset,
            color
        )
        lineDataset.circleColors = dotColors //IMPORTANT
        lineDataset.circleHoleColors = holeColors //IMPORTANT
        lineDataset.isHighlightEnabled = true
        lineDataset.setDrawHighlightIndicators(true)
        lineDataset.setDrawCircles(true)
        lineDataset.circleRadius = 6f
        lineDataset.circleHoleRadius = 3f
        lineDataset.setDrawCircleHole(true)
        lineDataset.setDrawValues(false)
        lineDataset.valueTextSize = 18f

        lineDataset.valueTextColor = color



        lineDataset.valueFormatter = object : ValueFormatter() {


            override fun getFormattedValue(value: Float): String {
                if (value == 0f || value == 16f) return ""
                return value.roundToInt().toString() + "DDDDD"
            }
        }

        lineDataset.setDrawHorizontalHighlightIndicator(false)
        lineDataset.setDrawVerticalHighlightIndicator(false)


    }




    fun changeCircleHoleColor(
        dataSet: LineDataSet,
        indexOfDot: Int,
        selected: Int,
        notSelected: Int? = null
    ) {


        for ((inc, value) in dataSet.circleHoleColor.withIndex()) {
            dataSet.circleHoleColors[inc] = notSelected
        }




        dataSet.circleHoleColors[indexOfDot] = selected


    }


}