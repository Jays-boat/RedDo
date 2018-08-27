package com.jayboat.reddo.ui.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.jayboat.reddo.ui.adapter.ShowItemAdapter
import com.jayboat.reddo.utils.*
import com.jayboat.reddo.viewmodel.DateViewModel
import com.jayboat.reddo.viewmodel.EntryViewModel
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_search.view.*
import org.jetbrains.anko.startActivity


class MainActivity : BaseActivity() {

    lateinit var type: String
    lateinit var mAdapter: ShowItemAdapter
    private val dateModel by lazy { ViewModelProviders.of(this@MainActivity).get(DateViewModel::class.java) }
    private val entryModel by lazy { ViewModelProviders.of(this@MainActivity).get(EntryViewModel::class.java) }
    private var isEdit = false

    private val searchInputer by lazy { MutableLiveData<String>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        type = TYPE_ALL

        observeModel()

        mAdapter = ShowItemAdapter(ArrayList(), type, entryModel, {
            if (!isEdit) {
                if (type == TYPE_TODO) {
                    startActivity<EditTodoActivity>("id" to it)
                } else {

                    startActivity(Intent(this@MainActivity, EditActivity::class.java)
                            .putExtra("id", it))
                }
            } else {
                entryModel.getEntryById(it).observe(this, Observer {
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