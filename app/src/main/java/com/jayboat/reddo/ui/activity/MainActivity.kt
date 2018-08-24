package com.jayboat.reddo.ui.activity

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
import com.jayboat.reddo.*
import com.jayboat.reddo.base.BaseActivity
import com.jayboat.reddo.ui.adapter.ShowItemAdapter
import com.jayboat.reddo.utils.*
import com.jayboat.reddo.viewmodel.DateViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_top.*
import kotlinx.android.synthetic.main.popup_search.view.*


class MainActivity : BaseActivity() {

    lateinit var type: String
    lateinit var mAdapter: ShowItemAdapter
    private var mData = ArrayList<String>()
    private val dateModel by lazy { ViewModelProviders.of(this@MainActivity).get(DateViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        type = TYPE_ALL
        mAdapter = ShowItemAdapter(mData, type)

        rv_main.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        dateModel.nowDate.observe(this, Observer { tv_main_date.text = it })

        iv_main_calendar.setOnClickListener {
            startActivity(Intent(this@MainActivity, CalendarActivity::class.java))
        }

        gp_main.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_all -> type = TYPE_ALL
                R.id.rb_essay -> type = TYPE_ESSAY
                R.id.rb_daily -> type = TYPE_DAILY
                R.id.rb_schedule -> type = TYPE_AGENDA
                R.id.rb_todo -> type = TYPE_TODO
            }
            mAdapter.changeType(type)
        }

        iv_main_search.setOnClickListener {
            getPopupWindow()
        }

        iv_main_add.setOnClickListener {
            startActivity(Intent(this@MainActivity,EditActivity::class.java))
        }
    }


    private fun getPopupWindow() {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.popup_search, null)
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
                        override fun afterTextChanged(s: Editable?) {
                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            // todo 监听关键字进行显示
                            mAdapter.changeType(TYPE_SEARCH)
                        }
                    })
                }
    }
}