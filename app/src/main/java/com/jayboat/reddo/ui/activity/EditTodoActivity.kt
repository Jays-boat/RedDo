package com.jayboat.reddo.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.Todo
import com.jayboat.reddo.ui.viewholder.CalendarEntryHolder
import com.jayboat.reddo.utils.TYPE_AGENDA
import com.jayboat.reddo.utils.TYPE_ESSAY
import com.jayboat.reddo.utils.show
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.activity_edit_todo.*
import kotlinx.android.synthetic.main.popup_more.view.*
import org.jetbrains.anko.contentView

class EditTodoActivity : AppCompatActivity() {

    private var e: Entry = Entry()
    private val entryModel by lazy { ViewModelProviders.of(this@EditTodoActivity).get(EntryViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_todo)

        val holder = CalendarEntryHolder(this.contentView!!, entryModel, 0)
        val observer = Observer<Entry> { e ->
            if (e == null) {
                return@Observer
            }
            this.e = e
            if (e.todoList.isEmpty() || e.todoList.last().describe.isNotBlank()) {
                e.todoList = e.todoList.toMutableList().apply { add(Todo(entryId = e.simpleEntry.id)) }
            }
            holder.initTodoView(e.todoList, true)

            if (et_title.text.toString().isBlank())
                et_title.setText(e.simpleEntry.title)

        }

        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            entryModel.getEntryById(id).observe(this, observer)
        } else {
            entryModel.createEntry {
                observe(this@EditTodoActivity, observer)
            }
        }

        iv_edit_back.setOnClickListener {
            finish()
        }
        iv_edit_down.setOnClickListener {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            val v: View? = currentFocus
            if (imm != null && v != null) {
                imm.hideSoftInputFromWindow(v.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        iv_edit_more.setOnClickListener {
            getPopupWindow()
        }
    }

    override fun onDestroy() {
        val list = e.todoList.toMutableList().filter { it.id != 0 && it.describe.isBlank() }
        entryModel.delTodoList(list)
        e.simpleEntry.title = et_title.text.toString()
        e.simpleEntry.detail = StringBuilder().apply {
            list.forEach { t ->
                append("[${"-".takeUnless { t.isActivate } ?: "√".takeIf { t.isDone }
                ?: ""}] ${t.describe}")
            }
        }.toString()
        entryModel.updateSimpleEntrys(e.simpleEntry)
        super.onDestroy()
    }

    private fun getPopupWindow() {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.popup_more, null)
        PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                .apply {
                    isOutsideTouchable = true
                    isFocusable = true
                    setBackgroundDrawable(ColorDrawable())
                    animationStyle = R.style.anim_bottom
                    showAtLocation(iv_edit_more, Gravity.BOTTOM, 0, 0)
                    view.apply {
                        tv_choose_todo.setOnClickListener {
                            show("现在就是待办编辑页哦(〃'▽'〃)")
                            dismiss()
                        }
                        tv_choose_agenda.setOnClickListener {
                            startActivity(Intent(this@EditTodoActivity,EditActivity::class.java).putExtra("type", TYPE_AGENDA))
                            dismiss()
                        }
                        tv_choose_essay.setOnClickListener {
                            startActivity(Intent(this@EditTodoActivity,EditActivity::class.java).putExtra("type", TYPE_ESSAY))
                            dismiss()
                        }
                        tv_choose_daily.setOnClickListener {
                            dismiss()
                            startActivity(Intent(this@EditTodoActivity, DailyCameraActivity::class.java))
                            finish()
                        }
                    }
                }
    }
}
