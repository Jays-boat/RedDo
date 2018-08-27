package com.jayboat.reddo.ui.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.ui.widget.TodoItemView
import com.jayboat.reddo.utils.getDataString
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.recycle_item_center_left.view.*

/*
 by Cynthia at 2018/8/22
 */

class LeftItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val shape by lazy {
        mapOf(
                SimpleEntry.EntryType.TODO to R.drawable.ic_item_todo,
                SimpleEntry.EntryType.ESSAY to R.drawable.ic_item_essay,
                SimpleEntry.EntryType.DAILY to R.drawable.ic_item_daily,
                SimpleEntry.EntryType.AGENDA to R.drawable.ic_item_agenda
        )
    }

    fun initData(data: Entry, isEdit: Boolean, vm: EntryViewModel) {
        val simpleEntry = data.simpleEntry
        val todoList = data.todoList
        val imgList = data.imgList
        itemView.apply {
            ll_todo_list.visibility = View.GONE
            iv_right_image.visibility = View.VISIBLE
            tv_right_title.visibility = View.VISIBLE
            if (simpleEntry.type == SimpleEntry.EntryType.TODO) {
                iv_right_image.visibility = View.GONE
                tv_right_title.visibility = View.GONE
                ll_todo_list.visibility = View.VISIBLE
                repeat(if (todoList.size < 4) todoList.size else 3) {
                    ll_todo_list.addView(TodoItemView(itemView.context).apply {
                        refreshView(todoList[it], vm)
                    })
                }
            }
            when {
                simpleEntry.type == SimpleEntry.EntryType.DAILY -> {
                    val bitmap = simpleEntry.detail?.split("|")?.get(0)
                    Glide.with(itemView).load(bitmap).into(iv_right_image)
                }
                imgList.isEmpty() -> iv_right_image.visibility = View.GONE
                else -> Glide.with(itemView).load(imgList[0].uri).into(iv_right_image)
            }
            tv_time_left.text = getDataString(simpleEntry.time)
            tv_right_title.text = simpleEntry.title
            iv_center_image.setImageDrawable(ContextCompat.getDrawable(itemView.context, shape[simpleEntry.type]!!))
            iv_delete.apply {
                visibility = if (isEdit) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }
}

