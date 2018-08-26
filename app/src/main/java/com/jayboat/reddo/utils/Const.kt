package com.jayboat.reddo.utils

import com.jayboat.reddo.room.bean.SimpleEntry.RedDate
import java.util.*

val nowDate: RedDate
    get() = RedDate().apply {
        val c = Calendar.getInstance()
        year = c[Calendar.YEAR]
        month = c[Calendar.MONTH] + 1
        day = c[Calendar.DAY_OF_MONTH]
        hour = c[Calendar.HOUR_OF_DAY]
        minute = c[Calendar.MINUTE]
        second = c[Calendar.SECOND]
    }

const val TYPE_ESSAY = "随笔"
const val TYPE_TODO = "待办"
const val TYPE_AGENDA = "日程"
const val TYPE_DAILY = "日常"
const val TYPE_ALL = "全部"
const val TYPE_SEARCH = "搜索"
