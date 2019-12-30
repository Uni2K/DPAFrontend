package com.project.app.Bases

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.app.R

class DesignBase {


    companion object {
        val BAR_QUESTION_TEXT2 = 1
        val COLOR_PRIMARY = R.color.primaryColor
        val COLOR_PRIMARY_DARK = R.color.primaryDarkColor
        val COLOR_PRIMARY_LIGHT = R.color.primaryLightColor

        val COLOR_SECONDARY = R.color.secondaryColor
        val COLOR_SECONDARY_DARK = R.color.secondaryDarkColor
        val COLOR_SECONDARY_LIGHT = R.color.secondaryLightColor

        val COLOR_THIRD = R.color.thirdColor
        val COLOR_THIRD_DARK = R.color.thirdDarkColor
        val COLOR_THIRD_LIGHT = R.color.thirdLightColor

        fun getColor(color: Int, context: Context): Int {
            return context.resources.getColor(color, null)
        }

        fun setNavigationBarColor(context: Context?, window: Window, color: Int) {
        if(context!=null)    window.navigationBarColor =
            getColor(color, context)

        }

        fun setStatusBarColor(context: Context?, window: Window, color: Int) {
          if(context!=null)  window.statusBarColor =
              getColor(color, context)

        }

        fun changeStatusBarIconColor(window: Window, dark: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decor: View = window.decorView
                if (dark) {
                    decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                } else {

                    decor.systemUiVisibility = 0
                }
            }
        }


        fun removeChartLabels(barChart: BarChart) {
            barChart.description = null
            barChart.setDrawGridBackground(false)
            barChart.setDrawValueAboveBar(false)
            barChart.setDrawMarkers(false)
            barChart.onTouchListener = null
            barChart.setNoDataText(null)
            barChart.legend.isEnabled = false

            barChart.axisLeft.setDrawLabels(false)
            barChart.axisRight.setDrawLabels(false)
            barChart.xAxis.setDrawLabels(false)
            barChart.axisRight.setDrawGridLines(false)
            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisLeft.setDrawAxisLine(false)
            barChart.axisRight.setDrawAxisLine(false)
            barChart.xAxis.setDrawAxisLine(false)

            barChart.xAxis.setDrawGridLines(false)
            barChart.setDrawBorders(false)
            barChart.setDrawMarkers(false)

        }

        fun removeChartLabels(barChart: PieChart) {
            barChart.description = null

            barChart.setDrawMarkers(false)
            barChart.onTouchListener = null
            barChart.setNoDataText(null)
            barChart.legend.isEnabled = false
            barChart.setDrawMarkers(false)
            barChart.setDrawCenterText(false)
            barChart.setDrawEntryLabels(false)

        }


        fun styleChartData(ident: Int, dataSet: BarDataSet) {

            when (ident) {
                BAR_QUESTION_TEXT2 -> {
                    dataSet.setColor(Color.WHITE, 255)
                    dataSet.setDrawValues(false)


                }
            }

        }

        fun removeChartLabels(barChart: LineChart) {
            barChart.description = null
            barChart.setDrawBorders(false)
            barChart.setDrawGridBackground(false)
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.setDrawAxisLine(false)
            barChart.axisLeft.setDrawGridLines(false)
            barChart.axisLeft.setDrawAxisLine(false)
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            //    barChart.xAxis.setDraw(false)
          //  barChart.setExtraOffsets(20f, 10f, 10f, 10f)
            barChart.setDrawMarkers(false)
            barChart.onTouchListener = null
            barChart.setNoDataText(null)
            barChart.legend.isEnabled = false

        }

        fun removeEverything(barChart: LineChart) {
            removeChartLabels(barChart)
            barChart.xAxis.isEnabled = false
            barChart.axisLeft.isEnabled = false
            barChart.axisRight.isEnabled = false

        }

        fun setUpLineDataSet(lineDataset: LineDataSet, color: Int) {
            lineDataset.setDrawCircles(true)
            lineDataset.setCircleColor(color)
            lineDataset.circleRadius = 3f
            lineDataset.lineWidth = 3f
            lineDataset.color = color
            lineDataset.setDrawValues(false)
            lineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataset.setDrawCircles(false)
            lineDataset.setDrawCircleHole(false)
            lineDataset.circleRadius = 0f
            lineDataset.circleHoleRadius = 0f
            lineDataset.cubicIntensity = 0.05f
            lineDataset.setDrawValues(false)

        }

        fun setUpPieDataSet(pieDataSet: PieDataSet, context: Context?) {
            if (context == null) return
            pieDataSet.setDrawValues(false)
            pieDataSet.sliceSpace = 1.2f
            pieDataSet.setColors(
                context.resources.getColor(R.color.color_a1, null),
                context.resources.getColor(R.color.color_a2, null)
            )

        }

        fun changeBottomAppBarColor(bnv: BottomAppBar?, color: Int) {
            if (bnv?.context != null) {
                bnv.backgroundTint = ColorStateList.valueOf(
                    getColor(
                        color,
                        bnv.context
                    )
                )
            }

    }

    fun changeFABBackgroundColor(fab: FloatingActionButton?, color: Int) {
        if (fab?.context != null) {
            fab.backgroundTintList = ColorStateList.valueOf(
                getColor(
                    color,
                    fab.context
                )
            )
        }
    }

    fun changeFABTextColor(fab: FloatingActionButton?, color: Int) {
        if (fab?.context != null) {
            fab.imageTintList = ColorStateList.valueOf(
                getColor(
                    color,
                    fab.context
                )
            )
        }
    }
}
}