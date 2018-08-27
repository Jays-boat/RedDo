package com.jayboat.reddo.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.jayboat.reddo.R
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.ui.adapter.ShowItemAdapter
import com.jayboat.reddo.utils.*
import com.jayboat.reddo.viewmodel.DateViewModel
import com.jayboat.reddo.viewmodel.EntryViewModel
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_text.*
import kotlinx.android.synthetic.main.popup_search.view.*
import org.jetbrains.anko.startActivity


class MainActivity : BaseActivity() {

    lateinit var type: String
    private lateinit var mAdapter: ShowItemAdapter
    private val dateModel by lazy { ViewModelProviders.of(this@MainActivity).get(DateViewModel::class.java) }
    private val entryModel by lazy { ViewModelProviders.of(this@MainActivity).get(EntryViewModel::class.java) }
    private var isEdit = false

    private val searchInputer by lazy { MutableLiveData<String>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        type = TYPE_ALL

        observeModel()

        mAdapter = ShowItemAdapter(ArrayList(), type, entryModel, { id,type ->
            if (!isEdit) {
                if (type == SimpleEntry.EntryType.DAILY){
                    startActivity(Intent(this@MainActivity, PlayingVideoActivity::class.java)
                            .putExtra("id", id))
                } else if (type == SimpleEntry.EntryType.TODO) {
                    startActivity<EditTodoActivity>("id" to id)
                } else {
                    startActivity(Intent(this@MainActivity, EditActivity::class.java)
                            .putExtra("id", id))
                }
            } else {
                entryModel.getEntryById(id).observe(this, Observer {
                    if (it == null) {
                        return@Observer
                    }
                    entryModel.delEntry(it.simpleEntry)
                })
            }
        }) { editTime(it) }

        rv_main.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator.changeDuration = 100
            itemAnimator.moveDuration = 200
        }

        dateModel.nowDate.observe(this, Observer { tv_main_date.text = it })

        iv_main_calendar.setOnClickListener {
            startCalendarActivity(this@MainActivity)
        }


        tv_main_date.setOnClickListener {
            rv_main.smoothScrollToPosition(0)
        }

        gp_main.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_all -> type = TYPE_ALL
                R.id.rb_essay -> type = TYPE_ESSAY
                R.id.rb_daily -> type = TYPE_DAILY
                R.id.rb_schedule -> type = TYPE_AGENDA
                R.id.rb_todo -> type = TYPE_TODO
            }
            observeModel()
        }

        iv_main_search.setOnClickListener {
            getPopupWindow()
        }

        iv_main_add.setOnClickListener {
            if (type == TYPE_DAILY) {
                startActivity(Intent(this@MainActivity, DailyCameraActivity::class.java))
            } else if (type == TYPE_TODO) {
                startActivity<EditTodoActivity>()
            } else {
                startActivity(Intent(this@MainActivity, EditActivity::class.java)
                        .putExtra("type", type))
            }
        }

        iv_main_edit.setOnClickListener {
            isEdit = !isEdit
            mAdapter.changeEdit(isEdit)
        }

        iv_main_setting.setOnClickListener{
            if (dl_main.isDrawerOpen(nv_more)){
                dl_main.closeDrawer(nv_more)
            } else {
                dl_main.openDrawer(nv_more)
            }
        }

        nv_more.setNavigationItemSelectedListener {
            when(it.title){
                resources.getString(R.string.about) -> {
                    Dialog(this@MainActivity).apply {
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
                    Dialog(this@MainActivity).apply {
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
                    AlertDialog.Builder(this@MainActivity).apply {
                        setCancelable(true)
                        setTitle("选择夜间模式方案")
                        setMessage("注：自动模式会根据系统时间调整哦")
                        setNeutralButton("常关") { _, _ -> spPut("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_NO) }
                        setNegativeButton("自动") { _, _ -> spPut("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_AUTO) }
                        setPositiveButton("常开") { _, _ -> spPut("NIGHT_MODE", AppCompatDelegate.MODE_NIGHT_YES) }
                    }.show()
                }
            }

            true
        }

        searchInputer.observe(this, entryModel.searchEntrys(Consumer {
                mAdapter.changeType(TYPE_SEARCH,it)
        }))

    }


    @SuppressLint("InflateParams")
    private fun getPopupWindow() {
        val view = LayoutInflater.from(this).inflate(R.layout.popup_search, null)
        PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .apply {
                    isOutsideTouchable = true
                    isFocusable = true
                    setBackgroundDrawable(ColorDrawable())
                    animationStyle = R.style.anim_popup
                    showAtLocation(cl_main, Gravity.TOP, 0, 0)
                    view.btn_cancel.setOnClickListener {
                        dismiss()
                        mAdapter.changeType(type)
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

    private fun editTime(data: Entry) {
        TimePickerBuilder(this@MainActivity, OnTimeSelectListener { date, _ ->
            data.simpleEntry.time = dateToRedDate(date)
            entryModel.updateEntry(data)
        })
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setDate(redDateToDate(data.simpleEntry.time))
                .setTitleText("它真正存在的时间(；′⌒`)")
                .build()
                .show()
    }

    private fun observeModel() {
        entryModel.entrys.observe(this, Observer {
            val list = it ?: ArrayList()
            if (type == TYPE_ALL)
                mAdapter.changeType(type, list)
        })
        entryModel.essayEntrys.observe(this, Observer {
            val list = it ?: ArrayList()
            if (type == TYPE_ESSAY)
                mAdapter.changeType(type, list)
        })
        entryModel.todoEntrys.observe(this, Observer {
            val list = it ?: ArrayList()
            if (type == TYPE_TODO)
                mAdapter.changeType(type, list)
        })
        entryModel.agendaEntrys.observe(this, Observer {
            val list = it ?: ArrayList()
            if (type == TYPE_AGENDA)
                mAdapter.changeType(type, list)
        })
        entryModel.dailyEntrys.observe(this, Observer {
            val list = it ?: ArrayList()
            if (type == TYPE_DAILY)
                mAdapter.changeType(type, list)
        })
    }
}