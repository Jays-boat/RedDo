package com.jayboat.reddo.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Todo
import kotlinx.android.synthetic.main.item_check_text.view.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

/**
 * Author: Hosigus
 * Bolg: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/23 23:24
 * Description: 能处理Todo的ViewHolder
 */
class TodoListHolder(v: View) : RecyclerView.ViewHolder(v) {
    fun initTodoView(todoList: List<Todo>) {
        itemView.findViewById<LinearLayout>(R.id.ll_item_todo).apply {
            for (i in childCount - 1..todoList.size) {
                val todo = todoList[i - 1]
                val v = LayoutInflater.from(context).inflate(R.layout.item_check_text, this, false).apply {
                    layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent)
                    checkbox_todo.setBackgroundResource(
                            (R.drawable.ic_checkbox_cancel).takeUnless { todo.isActivate }
                                    ?: (R.drawable.ic_checkbox_checked).takeIf { todo.isDone }
                                    ?: (R.drawable.ic_checkbox_normal))
                    tv_todo.text = todo.describe
                }
                addView(v)
            }
        }
    }
}