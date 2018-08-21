package com.jayboat.reddo.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.jayboat.reddo.appContext

val screenWidth by lazy {
    DisplayMetrics().apply {
        (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getMetrics(this)
    }.widthPixels
}
val screenHeight by lazy {
    DisplayMetrics().apply {
        (appContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getMetrics(this)
    }.heightPixels
}

private val density by lazy { appContext.resources.displayMetrics.density }
private val scaleDensity by lazy { appContext.resources.displayMetrics.scaledDensity }

fun dp(dp: Float) = dp * density + 0.5f
fun sp(sp: Float) = sp * scaleDensity + 0.5f