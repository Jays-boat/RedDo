package com.jayboat.reddo.ui.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.haibin.calendarview.Calendar
import com.jayboat.reddo.R
import com.jayboat.reddo.ui.adapter.CalendarRecycleAdapter
import com.jayboat.reddo.utils.dp
import com.jayboat.reddo.utils.fakeAll
import com.jayboat.reddo.utils.nowDate
import com.jayboat.reddo.viewmodel.EntryViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(EntryViewModel::class.java) }
    private lateinit var listener: Disposable

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        fakeAll(50, viewModel)

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
            adapter = CalendarRecycleAdapter(this@CalendarActivity, viewModel.getEntrysByDate(nowDate))
            addItemDecoration(object :RecyclerView.ItemDecoration(){
                private val p = Paint().apply {
                    color = Color.parseColor("#C4C4C4")
                    isAntiAlias = true
                    strokeWidth = dp(1f)
                }

                override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    super.onDrawOver(c, parent, state)
                    val top = if (parent.childCount > 0) {
                        parent.getChildAt(0).top
                    } else {
                        parent.top
                    }.toFloat()
                    c.drawLine(0f, top, parent.right.toFloat(), top + dp(2f), p)
                    for (i in 0 until parent.childCount) {
                        val child = parent.getChildAt(i)
                        val y = child.bottom.toFloat()
                        c.drawLine(child.left.toFloat(), y, child.right.toFloat(), y + dp(2f), p)
                    }
                }
            })
        }

        listener = viewModel.simpleEntrys.observeOn(Schedulers.io())
                .map { list ->
                    val map = mutableMapOf<String, Calendar>()
                    list.asSequence().groupBy { it.time.year to (it.time.month to it.time.day) }
                            .map { timeMap ->
                                Calendar().apply {
                                    timeMap.key.let {
                                        year = it.first
                                        month = it.second.first
                                        day = it.second.second
                                    }
                                    timeMap.value.groupBy { it.type }.forEach { t, tList ->
                                        addScheme(t.color, tList.size.toString())
                                    }
                                }
                            }.forEach {
                                map[it.toString()] = it
                            }
                    return@map map
                }.observeOn(AndroidSchedulers.mainThread()).subscribe {
                    if (it != null) {
                        calendar_view.setSchemeDate(it)
                    }
                }
    }

    override fun onDestroy() {
        listener.dispose()
        super.onDestroy()
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
