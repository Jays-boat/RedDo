package com.jayboat.reddo.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.jayboat.reddo.R
import com.jayboat.reddo.appContext
import com.jayboat.reddo.utils.dp
import com.jayboat.reddo.utils.screenHeight
import com.jayboat.reddo.utils.screenWidth
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition


/*
 by Cynthia at 2018/8/23
 */
class EditLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), OnPictureListener {

    private var mPictures: MutableList<TouchImageView> = ArrayList()
    private val tag = "EditLayout"
    private var mListener: OnPictureListener = this

    init {
        addView(EditText(context,null).apply {
            background = null
            width = screenWidth
            height = screenHeight
            hint = "写点什么想说的吧:)"
            gravity = Gravity.TOP
            setPadding(dp(15f).toInt(),dp(15f).toInt(),dp(15f).toInt(),dp(15f).toInt())
        })
    }


    override fun addPicture(urls:List<Uri>) {
        urls.forEach {
            addView(TouchImageView(context, null).apply {
                Glide.with(context)
                        .asBitmap()  // some .jpeg files are actually gif
                        .load(it)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                picturePro = resource.width / resource.height.toFloat()
                                setImageBitmap(resource)
                            }
                        })
                isClickable = true
            }.also {
                mPictures.add(it)
            })
        }
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
                    temp.postScale(scalePro,scalePro, (it.left + it.cHeight/2).toFloat(), (it.top + it.cHeight/2).toFloat())
                    imageMatrix = temp
                    scalePro = 1f
                }
            }
        }
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return false
    }

    fun getListener():OnPictureListener{
        return mListener
    }
}

interface OnPictureListener {
    fun addPicture(urls:List<Uri>)
    fun editPicture()
}