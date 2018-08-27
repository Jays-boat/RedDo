package com.jayboat.reddo.ui.activity

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.ui.adapter.CalendarEntryDecoration
import com.jayboat.reddo.ui.adapter.CalendarRecycleAdapter
import com.jayboat.reddo.utils.nowDate
import com.jayboat.reddo.utils.spPut
import com.jayboat.reddo.viewmodel.DateViewModel
import com.jayboat.reddo.viewmodel.EntryViewModel
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_main.tv_main_date
import kotlinx.android.synthetic.main.activity_main.iv_main_calendar
import kotlinx.android.synthetic.main.activity_main.iv_main_setting
import kotlinx.android.synthetic.main.activity_main.iv_main_add
import kotlinx.android.synthetic.main.activity_main.iv_main_search
import kotlinx.android.synthetic.main.dialog_text.*
import kotlinx.android.synthetic.main.popup_search.view.*
import org.jetbrains.anko.startActivity

fun startCalendarActivity(context: AppCompatActivity) {
    infix fun View.to(name: String) = Pair(this, name)
    context.apply {
        startActivity(Intent(this, CalendarActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this,
                        tv_main_date to "watch",
                        iv_main_calendar to "switch",
                        iv_main_setting to "setting",
                        iv_main_add to "add",
                        iv_main_search to "search"
                ).toBundle())
    }
}

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/21 23:24
 * Description: 日历页
 */
class CalendarActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProviders.of(this).get(EntryViewModel::class.java) }
    private val searchInputer by lazy { MutableLiveData<String>() }
    private var searchPopup: PopupWindow? = null

    private val searchAdapter by lazy {
        CalendarRecycleAdapter(this, viewModel, MutableLiveData()) { id, type ->
            if (type == SimpleEntry.EntryType.DAILY) {
                startActivity<PlayingVideoActivity>("id" to id)
            } else if (type == SimpleEntry.EntryType.TODO) {
                startActivity<EditTodoActivity>("id" to id)
            } else {
                startActivity<EditActivity>("id" to id)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        iv_setting.setOnClickListener{
            if (dl_main.isDrawerOpen(nv_more)){
                dl_main.closeDrawer(nv_more)
            } else {
                dl_main.openDrawer(nv_more)
            }
        }

        nv_more.setNavigationItemSelectedListener {
            when(it.title){
                resources.getString(R.string.about) -> {
                    Dialog(this@CalendarActivity).apply {
                        setCancelable(true)
                        setContentView(R.layout.dialog_text)
                        ib_dialog_back.setOnClickListener{
                            dismiss()
                        }
                        tv_title.text = it.title.toString()
                        tv_content.text = resources.getText(R.string.about_content)
                    }.show()
                }
                resources.getString(R.string.help) -> {
                    Dialog(this@CalendarActivity).apply {
                        setCancelable(true)
                        setContentView(R.layout.dialog_text)
                        ib_dialog_back.setOnClickListener{
                            dismiss()
                        }
                        tv_title.text = it.title.toString()
                        tv_content.text = resources.getText(R.string.help_content)
                    }.show()
                }
                resources.getString(R.string.night_time_setting) ->{
                    AlertDialog.Builder(this@CalendarActivity).apply {
                        setCancelable(true)
                        setTitle("选择夜间模式方案")
                        setMessage("注：自动模式会根据系统时间调整哦")
                        setNeutralButton("常关") { _, _ -> spPut("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_NO)
                            recreate()}
                        setNegativeButton("自动") { _, _ -> spPut("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_AUTO)
                            recreate()}
                        setPositiveButton("常开") { _, _ -> spPut("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_YES)
                            recreate()}
                    }.show()
                }
            }

            true
        }

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
            adapter = CalendarRecycleAdapter(this@CalendarActivity, viewModel,
                    viewModel.getEntrysByDate(nowDate)) { id, type ->
                if (type == SimpleEntry.EntryType.DAILY) {
                    startActivity(Intent(this@CalendarActivity, PlayingVideoActivity::class.java)
                            .putExtra("id", id))
                } else {
                    startActivity(Intent(this@CalendarActivity, EditActivity::class.java)
                            .putExtra("id", id))
                }
            }
            addItemDecoration(CalendarEntryDecoration())
        }

        iv_switch.apply {
            setOnClickListener {
                finishAfterTransition()
            }
        }

        ViewModelProviders.of(this).get(DateViewModel::class.java).nowDate
                .observe(this, Observer { tv_time.text = it })

        viewModel.schemeDates.observe(this, Observer {
            calendar_view.setSchemeDate(it)
        })

        iv_add.setOnClickListener {
            startActivity<EditActivity>()
        }

        iv_search.setOnClickListener {
            if (searchPopup?.isShowing == true) {
                searchPopup?.dismiss()
            } else {
                initPopup()
            }
        }

        viewModel.entrys.observe(this, Observer {  })
        val temp = MutableLiveData<List<Entry>>()
        searchAdapter.entryList = temp
        searchInputer.observe(this, viewModel.searchEntrys(Consumer {
            temp.value = it
        }))
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

    private fun initPopup() {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.popup_search, null)
        view.rv_search.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@CalendarActivity)
            adapter = searchAdapter
        }
        searchPopup = PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .apply {
                    isOutsideTouchable = true
                    isFocusable = true
                    setBackgroundDrawable(ColorDrawable())
                    animationStyle = R.style.anim_popup
                    showAtLocation(cl_calendar, Gravity.TOP, 0, 0)
                    view.btn_cancel.setOnClickListener {
                        dismiss()
                    }
                    view.et_search.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {}
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            searchInputer.value = s.toString()
                        }
                    })
                }
    }
}
