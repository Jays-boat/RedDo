package com.jayboat.reddo.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.absoluteValue

/**
 * Author: Hosigus
 * Blog: https://www.jianshu.com/u/c3bf1852cbd8
 * Date: 2018/8/25 22:58
 * Description: 解决原版RV横向滑动冲突的问题
 */
class NotAnnoyRecycleView : RecyclerView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private var isDragging = false
    private var point = 0f to 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                point = e.x to e.y
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    return false
                }

                val d = (point.first - e.x).absoluteValue to (point.second - e.y).absoluteValue
                if (d.first > d.second) {
                    isDragging = true
                    return false
                } else if (d.first > 100 || d.second > 100) {
                    point = d
                }
            }
        }
        return super.onTouchEvent(e)
    }
}