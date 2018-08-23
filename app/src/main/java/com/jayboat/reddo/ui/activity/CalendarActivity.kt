package com.jayboat.reddo.ui.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.haibin.calendarview.Calendar
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType.*
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        tv_month_year.paint.isFakeBoldText = true

        tv_month_year.setOnClickListener {
            if (calendar_view.isYearSelectLayoutVisible) {
                calendar_view.closeYearSelectLayout()
            } else {
                calendar_view.showYearSelectLayout(calendar_view.selectedCalendar.year)
            }
        }

        arrows_left.setOnClickListener { calendar_view.scrollToPre(true) }

        arrows_right.setOnClickListener { calendar_view.scrollToNext(true) }

        calendar_view.setOnMonthChangeListener { year, month -> tv_month_year.text = getDateStr(year, month) }
        calendar_view.setOnYearChangeListener { tv_month_year.text = getDateStr(it, calendar_view.selectedCalendar.month)}

        tempData()
    }

    fun tempData() {

        val viewModel = ViewModelProviders.of(this).get(EntryViewModel::class.java)



        val c = Calendar().apply {
            year = calendar_view.curYear
            month = calendar_view.curMonth
            day = calendar_view.curDay
            addScheme(ESSAY.color, "1")
            addScheme(TODO.color, "2")
        }
        val c2 = Calendar().apply {
            year = calendar_view.curYear
            month = calendar_view.curMonth
            day = 1
            addScheme(ESSAY.color, "1")
            addScheme(TODO.color, "2")
            addScheme(AGENDA.color, "3")
            addScheme(AGENDA.color, "4")
        }

        calendar_view.setSchemeDate(mapOf(c.toString() to c, c2.toString() to c2))
    }

    @SuppressLint("SetTextI18n")
    override fun onBackPressed() =
        if (calendar_view.isYearSelectLayoutVisible) {
            calendar_view.selectedCalendar.apply {
                tv_month_year.text = getDateStr(year, month)
            }
            calendar_view.closeYearSelectLayout()
        } else {
            super.onBackPressed()
        }

    private fun getDateStr(year: Int, month: Int) = "${month}月 $year"
}
