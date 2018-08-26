package com.jayboat.reddo.utils

import android.util.Log
import com.jayboat.reddo.room.bean.SimpleEntry
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/*
 by Cynthia at 2018/8/25
 description: 日期转换成按照主页显示的类...
 */

val formatToDate by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.CHINA) }
val formatToString by lazy { SimpleDateFormat("M/ddE", Locale.CHINA) }
val nowYear by lazy { SimpleDateFormat("yyyy", Locale.CHINA).format(Date()) }

fun getDataString(date: SimpleEntry.RedDate):String {
    val temp =  "${date.year}-${date.month}-${date.day}"
    val temp1 = formatToString.format(formatToDate.parse(temp))
    return if (nowYear == date.year.toString()) "${date.hour}:${date.minute}\n$temp1" else "${date.hour}:${date.minute}\n${date.year}/$temp1"
}
