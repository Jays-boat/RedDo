package com.jayboat.reddo.utils

import com.jayboat.reddo.room.bean.SimpleEntry
import java.text.SimpleDateFormat
import java.util.*


/*
 by Cynthia at 2018/8/25
 description: 日期转换
 */

val formatToDate by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
val formatToString by lazy { SimpleDateFormat("M/ddE", Locale.CHINA) }
val formatDate by lazy { SimpleDateFormat("yyyy-MM-dd HH-mm", Locale.CHINA) }
val mCalendar: Calendar by lazy { Calendar.getInstance() }
val nowYear: String by lazy { SimpleDateFormat("yyyy", Locale.CHINA).format(Date()) }
val redDate = SimpleEntry.RedDate()

fun getDataString(date: SimpleEntry.RedDate): String {
    val temp = "${date.year}-${date.month}-${date.day}"
    val temp1 = formatToString.format(formatToDate.parse(temp))
    val temp2 = String.format("%02d:%02d", date.hour,date.minute)
    return if (nowYear == date.year.toString()) "$temp2\n$temp1" else "$temp2\n${date.year}/$temp1"
}

fun redDateToDate(date: SimpleEntry.RedDate): Calendar {
    val temp = "${date.year}-${date.month}-${date.day} ${date.hour}-${date.minute}"
    mCalendar.time = formatDate.parse(temp)
    return mCalendar
}

fun dateToRedDate(date: Date): SimpleEntry.RedDate {
    mCalendar.time = date
    redDate.year = mCalendar.get(Calendar.YEAR)
    redDate.month = mCalendar.get(Calendar.MONTH) + 1
    redDate.day = mCalendar.get(Calendar.DAY_OF_MONTH)
    redDate.hour = mCalendar.get(Calendar.HOUR)
    redDate.minute = mCalendar.get(Calendar.MINUTE)
    redDate.second = 0
    return redDate
}
