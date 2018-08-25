package com.jayboat.reddo.ui.view.calendar

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.TextView
import com.haibin.calendarview.WeekBar
import com.jayboat.reddo.R

class HWeekBarView(context: Context) : WeekBar(context) {
    private val weeks: Array<String> = context.resources.getStringArray(R.array.week_bar_array)

    init {
        LayoutInflater.from(context).inflate(com.haibin.calendarview.R.layout.cv_week_bar, this, true).apply {
            setBackgroundColor(Color.WHITE)
            val textColor = ContextCompat.getColor(context, R.color.week_bar_text_color)
            for (i in 0 until childCount) {
                (getChildAt(i) as TextView).apply {
                    setTextColor(textColor)
                    paint.isFakeBoldText = true
                }
            }
        }
    }

    override fun onWeekStartChange(weekStart: Int) {
        for (i in 0 until childCount) {
            (getChildAt(i) as TextView).text = weeks[i]
        }
    }

}