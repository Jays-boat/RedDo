package com.jayboat.reddo.utils

import android.content.Context
import android.content.SharedPreferences
import com.jayboat.reddo.appContext

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/27 18:15
 * Description: com.jayboat.reddo.utils
 */
val Context.defaultSp get() = sp("RedDoDefault")

val defaultSp get() = appContext.sp("RedDoDefault")

fun Context.sp(name: String): SharedPreferences = getSharedPreferences(name, Context.MODE_PRIVATE)

fun sp(name: String) = appContext.sp(name)

fun spGet(name: String, defaultValue: Int = 0) = defaultSp.getInt(name, defaultValue)

fun spPut(name: String, value: Int) = defaultSp {
    putInt(name,value)
}

operator fun SharedPreferences.invoke(modify: SharedPreferences.Editor.() -> Unit) = edit().apply(modify).apply()