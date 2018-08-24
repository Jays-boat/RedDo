package com.jayboat.reddo.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.jayboat.reddo.R
import com.jayboat.reddo.utils.dp
import com.jayboat.reddo.utils.screenHeight
import com.jayboat.reddo.utils.screenWidth

/*
 by Cynthia at 2018/8/23
 */
class EditLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), OnPictureListener {

    private var mPictures: MutableList<TouchImageView> = ArrayList()
    private val tag = "EditLayout"

    init {
        addView(EditText(context,null).apply {
            background = null
            width = screenWidth
            height = screenHeight
            hint = "写点什么想说的吧:)"
            gravity = Gravity.TOP
            setPadding(dp(5f).toInt(),dp(5f).toInt(),dp(5f).toInt(),dp(5f).toInt())
        })

        addView(TouchImageView(context, null).apply {
            val src = ContextCompat.getDrawable(context, R.drawable.temp)
            picturePro = src!!.intrinsicWidth / src.intrinsicHeight.toFloat()
            setImageDrawable(src)
            isClickable = true
        }.also {
            mPictures.add(it)
        })

//        addView(TouchImageView(context, null).apply {
//            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.temp1))
//            isClickable = true
//        }.also {
//            mPictures.add(it)
//        })

    }


    override fun addPicture() {

    }

    override fun editPicture() {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasure = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var temp : Matrix
        mPictures.forEach {
            it.apply {
                x = xLocation
                y = yLocation
                if (scalePro != 0f){
                    temp = imageMatrix
                    temp.postScale(scalePro,scalePro)
                    imageMatrix = temp
                }


            }
        }
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return false
    }
}

interface OnPictureListener {
    fun addPicture()
    fun editPicture()
}