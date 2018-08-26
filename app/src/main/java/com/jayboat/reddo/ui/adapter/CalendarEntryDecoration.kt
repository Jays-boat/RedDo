package com.jayboat.reddo.ui.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import com.jayboat.reddo.utils.dp

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/25 15:35
 * Description: 日历页下方RV分割线
 */
class CalendarEntryDecoration : RecyclerView.ItemDecoration() {
    private val p = Paint().apply {
        color = Color.parseColor("#C4C4C4")
        isAntiAlias = true
        strokeWidth = dp(1f)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        c.drawLine(0f, 0f, parent.right.toFloat(), dp(2f), p)
        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val y = child.bottom.toFloat()
            c.drawLine(child.left.toFloat(), y, child.right.toFloat(), y + dp(2f), p)
        }
    }
}