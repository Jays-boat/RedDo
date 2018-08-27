package com.jayboat.reddo.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Todo
import com.jayboat.reddo.ui.widget.TodoItemView
import com.jayboat.reddo.viewmodel.EntryViewModel
import org.jetbrains.anko.collections.forEachWithIndex

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/23 23:24
 * Description: 能处理Todo的ViewHolder
 */
class CalendarEntryHolder(v: View, private val vm: EntryViewModel, private val start: Int = 2) : RecyclerView.ViewHolder(v) {
    fun initTodoView(todoList: List<Todo>, editable: Boolean = false) {
        itemView.findViewById<LinearLayout>(R.id.ll_item_todo).apply {
            for (i in childCount - start until todoList.size) {
                addView(TodoItemView(context))
            }
            todoList.forEachWithIndex { i, t ->
                (getChildAt(start + i) as? TodoItemView)?.refreshView(t, vm, editable)
            }
            for (i in todoList.size + start until childCount) {
                removeViewAt(i)
            }
        }
    }
}