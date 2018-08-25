package com.jayboat.reddo.ui.adapter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType
import com.jayboat.reddo.ui.viewholder.TodoListHolder
import com.jayboat.reddo.utils.dp
import kotlinx.android.synthetic.main.recycle_item_center_right.view.*
import org.jetbrains.anko.*

/**
 * Author: Hosigus
 * Bolg: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/24 0:15
 * Description: 日历列表RV的Adapter
 */
class CalendarRecycleAdapter(
        private val context: AppCompatActivity,
        entryList: LiveData<List<Entry>>
) : RecyclerView.Adapter<TodoListHolder>() {
    var entryList: LiveData<List<Entry>> = entryList
        set(value) {
            field.removeObserver(listener)
            field = value
            field.observe(context,listener)
            notifyDataSetChanged()
        }

    private val listener = Observer<List<Entry>> {
        notifyDataSetChanged()
    }
    init {
        this.entryList.observe(context, listener)
    }

    override fun getItemViewType(position: Int) = entryList.value?.get(position)?.simpleEntry?.type?.ordinal ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= TodoListHolder(
        parent.context.relativeLayout {
            val time =  textView {
                id = R.id.tv_item_todo_time
                compoundDrawablePadding = dp(25f).toInt()
                textSize = 12f
            }.lparams(width = wrapContent, height = wrapContent){
                marginStart = dp(5f).toInt()
                centerVertically()
            }
            if (SimpleEntry.EntryType.values()[viewType] == EntryType.TODO) {
                verticalLayout {
                    id = R.id.ll_item_todo
                    textView {
                        id = R.id.tv_item_todo_title
                        textSize = 14f
                        textColorResource = R.color.calendar_rv_text
                    }.lparams(width = matchParent, height = wrapContent)
                    view {
                        backgroundResource = R.drawable.line
                    }.lparams(width = dp(200f).toInt(), height = dp(2f).toInt()) {
                        verticalMargin = dp(4f).toInt()
                    }
                }.lparams(width = matchParent, height = wrapContent) {
                    marginStart = dp(50f).toInt()
                    centerVertically()
                    endOf(time)
                }
            } else {
                textView {
                    id = R.id.tv_item_todo_title
                    textSize = 14f
                    textColorResource = R.color.calendar_rv_text
                }.lparams(width = matchParent, height = wrapContent) {
                    marginStart = dp(50f).toInt()
                    endOf(time)
                }
            }
            lparams(matchParent, wrapContent){
                verticalPadding = dp(20f).toInt()
                horizontalMargin = dp(15f).toInt()

            }
        }
    )

    override fun getItemCount() = entryList.value?.size ?: 0

    override fun onBindViewHolder(holder: TodoListHolder, position: Int) {
        val entry = entryList.value?.get(position) ?: return
        val simpleEntry = entry.simpleEntry

        holder.itemView.apply {
            val time = findViewById<TextView>(R.id.tv_item_todo_time)
            val title = findViewById<TextView>(R.id.tv_item_todo_title)
            time.setCompoundDrawables(
                    ContextCompat.getDrawable(context, when (simpleEntry.type) {
                        EntryType.ESSAY -> R.drawable.ic_point_essay
                        EntryType.TODO -> R.drawable.ic_point_todo
                        EntryType.AGENDA -> R.drawable.ic_point_agenda
                        EntryType.DAILY -> R.drawable.ic_point_daily
                    })?.apply {
                        setBounds(0, 0, 20, 20)
                    }
                    , null, null, null)
            time.textColor = simpleEntry.type.color
            time.text = String.format("%02d:%02d", simpleEntry.time.hour, simpleEntry.time.minute)
            title.text = simpleEntry.title
        }

        if (simpleEntry.type == EntryType.TODO) {
            holder.initTodoView(entry.todoList)
        }

    }

}