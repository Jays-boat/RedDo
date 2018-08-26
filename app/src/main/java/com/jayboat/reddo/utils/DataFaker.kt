package com.jayboat.reddo.utils

import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType.*
import com.jayboat.reddo.room.bean.Todo
import com.jayboat.reddo.viewmodel.EntryViewModel
import io.reactivex.schedulers.Schedulers

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/24 13:40
 * Description: 伪造测试数据，向数据库加入一堆东西
 */

/**
 * 各种数据，随机加
 * @param size 伪造数量
 */
fun fakeAll(size: Int, vm: EntryViewModel) = Schedulers.newThread().scheduleDirect {
    val list = mutableListOf<Entry>()
    val types = values()
    repeat(size) {
        list.add(Entry().apply {
            val sb = StringBuilder()
            simpleEntry = SimpleEntry().apply {
                type = types[(Math.random() * types.size).toInt()]
                title = "在下是一条伪造的$type"
                time.year -= (Math.random() * 2).toInt()
                time.month = (Math.random() * 12).toInt()
                time.day = (Math.random() * 20).toInt()
            }
            when (simpleEntry.type) {
                TODO -> {
                    val mTodoList = mutableListOf<Todo>()
                    repeat((Math.random() * 5).toInt()) { i ->
                        mTodoList.add(Todo("${i + 1}. ${simpleEntry.time.day}假装有事要做,告辞"))
                    }
                    todoList = mTodoList
                    mTodoList.forEach { t ->
                        sb.append("[${"-".takeUnless { t.isActivate } ?: "√".takeIf { t.isDone }
                        ?: ""}] ${t.describe}")
                    }
                }
                ESSAY, AGENDA ->
                    //todo 加图片和位置
                    repeat((Math.random() * 5).toInt()) { i ->
                        sb.append("假装我有详情信息,这是第${i + 1}行\n")
                    }
                DAILY -> {
                    //todo 加视频地址
                }
            }
            simpleEntry.detail = sb.toString()
        })
    }
    vm.insertEntrys(*list.toTypedArray())
}
