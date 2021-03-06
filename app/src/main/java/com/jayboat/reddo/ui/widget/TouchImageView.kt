package com.jayboat.reddo.ui.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import com.jayboat.reddo.R
import com.jayboat.reddo.utils.screenHeight
import com.jayboat.reddo.utils.screenWidth
import kotlinx.android.synthetic.main.dialog_delete.*

/*
 by Cynthia at 2018/8/23
 */
class TouchImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
    private var dx = 0.0f
    private var dy = 0.0f
    private var originDistance = 1f
    private val defaultMode = 0
    private val moveMode = 1
    private val scaleMode = 2
    private var currentMode = -1
    private val tag = "TouchImageView"
    private var mRunnable: LongPressRunnable? = null

    var xLocation = 0f
    var yLocation = 0f
    var scalePro = 1f
    var picturePro = 0f
    var cWidth = 0
    var cHeight = 0
    var isDelete = false
    var mUri:String = ""

    init {
        scaleType = ScaleType.FIT_XY
        isSaveFromParentEnabled = true
        setBackgroundColor(Color.MAGENTA)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mWidth: Int
        val mHeight: Int
        val widthMeasure: Int
        val heightMeasure: Int
        if (cWidth == 0 || cHeight == 0) {
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


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dx = event.x
                dy = event.y
                mRunnable = LongPressRunnable()
                postDelayed(mRunnable, 1000)
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount < 2) {
                    val offsetX = event.x - dx
                    val offsetY = event.y - dy
                    if (Math.abs(offsetX) > 10 || Math.abs(offsetY) > 10){
                        if (mRunnable != null) {
                            removeCallbacks(mRunnable)
                        }
                        currentMode = moveMode
                        left += offsetX.toInt()
                        top += offsetY.toInt()
                        xLocation = x
                        yLocation = y
                        requestLayout()
                    }
//                    layout((left + offsetX).toInt(), (top + offsetY).toInt(), (right + offsetX).toInt(), (bottom + offsetY).toInt())

                } else {
                    if (mRunnable != null) {
                        removeCallbacks(mRunnable)
                    }
                    currentMode = scaleMode
                    val currentDistance = distance(event)
                    if (originDistance == 1f) {
                        originDistance = currentDistance
                    } else {
                        if (currentDistance > 10f) {
                            scalePro = currentDistance / originDistance
                            Log.i("scale", "$scalePro")
                            xLocation = x
                            yLocation = y
                            originDistance = currentDistance
                            requestLayout()
                        }
                    }

                }
            }
            MotionEvent.ACTION_UP -> {
                if (mRunnable != null) {
                    removeCallbacks(mRunnable)
                }
                when (currentMode) {
                    moveMode -> {
                        Log.i(tag, "moving...")
                        currentMode = defaultMode
                        return true
                    }
                    scaleMode -> {
                        Log.i(tag, "scaling...")
                        currentMode = defaultMode
                        originDistance = 1f
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
    }

    private fun distance(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    inner class LongPressRunnable : Runnable {
        override fun run() {
            Dialog(context).apply {
                setContentView(R.layout.dialog_delete)
                tv_dialog_sure.setOnClickListener {
                    isDelete = true
                    requestLayout()
                    this.dismiss()
                }
                tv_dialog_cancel.setOnClickListener { this.dismiss() }
            }.show()
        }
    }
}

