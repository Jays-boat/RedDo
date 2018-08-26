package com.jayboat.reddo.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.EditText
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.Image
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.room.bean.SimpleEntry.EntryType.*
import com.jayboat.reddo.utils.*


/*
 by Cynthia at 2018/8/23
 */
class EditLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), OnPictureListener {

    private val tag = "EditLayout"

    private var type : String = ""
    private var mPictures: MutableList<TouchImageView> = mutableListOf()
    private var mListener: OnPictureListener = this
    private var mEditText: EditText = EditText(context, null).apply {
        tag = "myEditText"
        background = null
        width = screenWidth
        height = screenHeight
        hint = "写点什么想说的吧:)"
        gravity = Gravity.TOP
        setPadding(dp(15f).toInt(), dp(15f).toInt(), dp(15f).toInt(), dp(15f).toInt())
    }

    init {
        addView(mEditText)
        type = TYPE_ESSAY
    }

    override fun loadData(data:Entry) {
        type = when(data.simpleEntry.type){
            ESSAY -> TYPE_ESSAY
            TODO -> TYPE_TODO
            AGENDA -> TYPE_AGENDA
            DAILY -> TYPE_DAILY
        }
    }

    override fun addPicture(urls: List<Uri>) {
        urls.forEach {
            addView(TouchImageView(context, null).apply {
                Glide.with(context)
                        .asBitmap()
                        .load(it)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                picturePro = resource.width / resource.height.toFloat()
                                setImageBitmap(resource)
                            }
                        })
                this.mUri = it
                isClickable = true
            }.also {
                mPictures.add(it)
            })
        }
    }

    override fun saveData()= Entry().apply {
        simpleEntry.title = mEditText.text.split("\n")[0]
        simpleEntry.detail = mEditText.text.toString()
        simpleEntry.type = when(type){
            TYPE_ESSAY -> ESSAY
            TYPE_AGENDA -> AGENDA
            TYPE_DAILY -> DAILY
            else -> TODO
        }
        imgList = mPictures.map {
            Image().apply {
                this.xLocation = it.xLocation
                this.yLocation = it.xLocation
                this.width = it.cWidth
                this.height = it.cHeight
                this.uri = it.mUri.toString()
            }
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasure = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY)
        val heightMeasure = MeasureSpec.makeMeasureSpec(screenHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasure, heightMeasure)
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        var temp: Matrix
        mPictures.forEach {
            if (it.isDelete) {
                removeView(it)
                return@forEach
            }
            it.apply {
                x = xLocation
                y = yLocation
                if (scalePro != 0f) {
                    temp = imageMatrix
                    temp.postScale(scalePro, scalePro, (it.left + it.cHeight / 2).toFloat(), (it.top + it.cHeight / 2).toFloat())
                    imageMatrix = temp
                    scalePro = 1f
                }
            }
        }
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return false
    }

    fun getListener(): OnPictureListener {
        return mListener
    }
}

interface OnPictureListener {
    fun loadData(data:Entry)
    fun addPicture(urls: List<Uri>)
    fun saveData(): Entry
}