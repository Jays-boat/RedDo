package com.jayboat.reddo.ui.activity

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jayboat.reddo.R
import com.jayboat.reddo.ui.adapter.CalendarEntryDecoration
import com.jayboat.reddo.ui.adapter.CalendarRecycleAdapter
import com.jayboat.reddo.utils.nowDate
import com.jayboat.reddo.viewmodel.DateViewModel
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.include_top.*

fun startCalendarActivity(context: AppCompatActivity,top: View) {
    context.startActivity(Intent(context, CalendarActivity::class.java),
            ActivityOptions.makeSceneTransitionAnimation(context, top, "top").toBundle())
}

class CalendarActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(EntryViewModel::class.java) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

//        fakeAll(50,viewModel)

        tv_month_year.apply {
            paint.isFakeBoldText = true
            setOnClickListener {
                if (calendar_view.isYearSelectLayoutVisible) {
                    onBackPressed()
                } else {
                    calendar_view.showYearSelectLayout(calendar_view.selectedCalendar.year)
                }
            }
        }

        arrows_left.setOnClickListener { calendar_view.scrollToPre(true) }

        arrows_right.setOnClickListener { calendar_view.scrollToNext(true) }

        calendar_view.apply {
            setOnDateSelectedListener { calendar, _ ->
                (rv_calendar.adapter as CalendarRecycleAdapter).entryList = viewModel.getEntrysByDate(calendar)
            }
            setOnMonthChangeListener { year, month ->
                tv_month_year.text = getDateStr(year, month)
            }
            setOnYearChangeListener {
                tv_month_year.text = getDateStr(it, calendar_view.selectedCalendar.month)
            }
        }

        rv_calendar.apply {
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = CalendarRecycleAdapter(this@CalendarActivity, viewModel, viewModel.getEntrysByDate(nowDate))
            addItemDecoration(CalendarEntryDecoration())
        }

        iv_main_calendar.apply {
            setImageResource(R.drawable.ic_list)
            setOnClickListener {
                finish()
            }
        }

        ViewModelProviders.of(this).get(DateViewModel::class.java).nowDate
                .observe(this, Observer { tv_main_date.text = it })

        viewModel.schemeDates.observe(this, Observer {
            calendar_view.setSchemeDate(it)
        })
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
