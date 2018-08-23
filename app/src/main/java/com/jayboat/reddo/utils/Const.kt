package com.jayboat.reddo.utils

import com.jayboat.reddo.room.bean.SimpleEntry.RedDate
import java.util.Calendar

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