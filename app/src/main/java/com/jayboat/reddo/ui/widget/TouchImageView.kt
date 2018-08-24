package com.jayboat.reddo.ui.widget

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.jayboat.reddo.utils.screenHeight
import com.jayboat.reddo.utils.screenWidth
import com.jayboat.reddo.utils.sp

/*
 by Cynthia at 2018/8/23
 */
class TouchImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
    private var dx = 0.0f
    private var dy = 0.0f
    private var originDistance = 0f
    private val defaultMode = 0
    private val moveMode = 1
    private val scaleMode = 2
    private var currentMode = -1
    private var isScale = false
    private val tag = "TouchImageView"

    var xLocation = 0f
    var yLocation = 0f
    var scalePro = 0f
    var picturePro = 0f
    var cWidth = 0
    var cHeight = 0

    init {
        scaleType = ScaleType.FIT_XY
        isSaveFromParentEnabled = true
        setBackgroundColor(Color.MAGENTA)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mWidth = MeasureSpec.getSize(widthMeasureSpec)
        var mHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthMeasure: Int
        val heightMeasure: Int
        if (cWidth == 0 || cHeight == 0 || scalePro == 0f) {
            mWidth = screenWidth / 3
            mHeight = (mWidth / picturePro).toInt()
            widthMeasure = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY)
            heightMeasure = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY)
            setMeasuredDimension(mWidth, mHeight)
        } else {
            mWidth = (cWidth.toFloat() * scalePro).toInt()
            mHeight = (cHeight.toFloat() * scalePro).toInt()
            widthMeasure = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY)
            heightMeasure = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY)
            setMeasuredDimension(mWidth, mHeight)
        }
        super.onMeasure(widthMeasure, heightMeasure)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dx = event.x
                dy = event.y
                if (event.pointerCount >= 2){
                    originDistance = distance(event)
                    Log.i("two","point down $originDistance")
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount < 2) {
                    currentMode = moveMode
                    var offsetX = event.x - dx
                    var offsetY = event.y - dy
                    if (left < 0 || right + offsetX > screenWidth) {
                        offsetX = 0f
                    }
                    if (top < 0 || bottom + offsetY > screenHeight) {
                        offsetY = 0f
                    }
                    left += offsetX.toInt()
                    top += offsetY.toInt()
                    xLocation = x
                    yLocation = y
//                    layout((left + offsetX).toInt(), (top + offsetY).toInt(), (right + offsetX).toInt(), (bottom + offsetY).toInt())
                    requestLayout()
                } else {
                    currentMode = scaleMode
                    if(!isScale){
                        isScale = true
                        originDistance = distance(event)
                    } else {
                        val currentDistance = distance(event)
                        if (currentDistance > 10f) {
                            scalePro = currentDistance / originDistance
                            Log.i("scale", "$scalePro")
                            requestLayout()
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                when (currentMode) {
                    moveMode -> {
//                        Log.i(tag, "moving...")
                        currentMode = defaultMode
                        return true
                    }
                    defaultMode -> {
                        //todo 展示大图
                        Log.i(tag, "you touch this image!")
                        return true
                    }
                    scaleMode -> {
//                        Log.i(tag, "scaling...")
                        currentMode = defaultMode
                        isScale = false
                        return true
                    }
                    else -> {
                    }
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        cWidth = width
        cHeight = height
        Log.i("scale onDraw", "width:$width,height:$height")
    }

    private fun distance(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}