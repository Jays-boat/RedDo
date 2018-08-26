package com.jayboat.reddo.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Todo
import com.jayboat.reddo.ui.widget.TodoItemView
import com.jayboat.reddo.viewmodel.EntryViewModel

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/23 23:24
 * Description: 能处理Todo的ViewHolder
 */
class CalendarEntryHolder(v: View, private val vm: EntryViewModel) : RecyclerView.ViewHolder(v) {
    fun initTodoView(todoList: List<Todo>) {
        itemView.findViewById<LinearLayout>(R.id.ll_item_todo).apply {
            for (i in childCount - 1..todoList.size) {
                addView(TodoItemView(context))
            }
            for (i in 2..todoList.size) {
                (getChildAt(i) as TodoItemView).refreshView(todoList[i - 2], vm)
            }
            for (i in todoList.size + 1 until childCount) {
                removeViewAt(i)
            }
        }
    }
}