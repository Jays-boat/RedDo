package com.jayboat.reddo.ui.view.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView
import com.jayboat.reddo.R

class HMonthView(context: Context) : MonthView(context) {

    private var mSelectRadius = 0f
    private var mTextWidth = 0f

    private val mSelectStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = 5f
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.calendar_select)
    }
    private val mSchemeLinePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        strokeWidth = 5f
    }

    override fun onDrawText(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean, isSelected: Boolean) {
        val cx = x + mItemWidth / 2f
        val top = mTextBaseLine + y

        if (calendar.isCurrentMonth) {
            canvas.drawText(calendar.day.toString(), cx, top,
                    if (calendar.isWeekend) mOtherMonthTextPaint else mCurMonthTextPaint)
        }

    }

    override fun onPreviewHook() {
        super.onPreviewHook()
        mSelectRadius = mCurMonthTextPaint.textSize / 4f * 3
        mTextWidth = mCurMonthLunarTextPaint.measureText("0") * 2f
    }

    override fun onDrawSelected(canvas: Canvas, calendar: Calendar?, x: Int, y: Int, hasScheme: Boolean): Boolean {
        canvas.drawCircle(x + mItemWidth / 2f, y + mItemHeight / 2f, mSelectRadius, mSelectStrokePaint)
        return true
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        if (!calendar.isCurrentMonth) {
            return
        }
        var perBarWidth = mTextWidth
        val maxWidth = calendar.schemes.maxBy { it.scheme.toInt() }?.scheme?.toInt()?.let {
            if (it < 5) perBarWidth *= 1 - it / 10f
            it * perBarWidth
        }?.toInt()
        val xStart = x + (mItemWidth - Math.min(mItemWidth, maxWidth ?: mItemWidth)) / 2f
        val yTop = y + mItemHeight / 2 + mSelectRadius
        calendar.schemes.forEachIndexed { index, scheme ->
            mSchemeLinePaint.color = scheme.shcemeColor
            val lineWidth = scheme.scheme.toInt() * perBarWidth
            val yInLine = yTop + mSchemeLinePaint.strokeWidth * index * 1.5f
            canvas.drawLine(xStart, yInLine, xStart + lineWidth, yInLine, mSchemeLinePaint)
        }
    }
}