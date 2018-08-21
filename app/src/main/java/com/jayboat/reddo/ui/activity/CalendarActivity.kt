package com.jayboat.reddo.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.haibin.calendarview.Calendar
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Entry
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        tv_month_year.paint.isFakeBoldText = true

        val c = Calendar().apply {
            year = calendar_view.curYear
            month = calendar_view.curMonth
            day = calendar_view.curDay
            addScheme(Entry.EntryType.ESSAY.color, "1")
            addScheme(Entry.EntryType.TODO.color, "2")
        }
        val c2 = Calendar().apply {
            year = calendar_view.curYear
            month = calendar_view.curMonth
            day = 1
            addScheme(Entry.EntryType.ESSAY.color, "1")
            addScheme(Entry.EntryType.TODO.color, "2")
            addScheme(Entry.EntryType.AGENDA.color, "3")
            addScheme(Entry.EntryType.AGENDA.color, "4")
        }

        calendar_view.setSchemeDate(mapOf(c.toString() to c, c2.toString() to c2))
        calendar_view.showYearSelectLayout(calendar_view.curYear)
        calendar_view.scrollToCurrent()
    }
}
