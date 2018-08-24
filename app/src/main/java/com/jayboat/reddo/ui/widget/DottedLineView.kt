package com.jayboat.reddo.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/*
 by Cynthia at 2018/8/21
 */
 class DottedLineView(context: Context?, attrs: AttributeSet?) : View(context, attrs){
    private val mPaint = Paint()
    private val mPath = Path()

    init {
        mPaint.apply {
            isAntiAlias = true
            color = Color.parseColor("#EAEAEA")
            style = Paint.Style.STROKE
            strokeWidth = 1f
            pathEffect = DashPathEffect(floatArrayOf(10f,10f),0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath.apply {
            reset()
            moveTo(width / 2f,0f)
            lineTo(width / 2f, height.toFloat())
        }
        canvas.drawPath(mPath,mPaint)
    }
}
