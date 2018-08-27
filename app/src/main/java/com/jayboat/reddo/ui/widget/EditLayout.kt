package com.jayboat.reddo.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.Image
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.utils.dp
import com.jayboat.reddo.utils.getImageAbsolutePath
import com.jayboat.reddo.utils.screenHeight
import com.jayboat.reddo.utils.screenWidth
import com.jayboat.reddo.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.activity_edit.view.*


/*
 by Cynthia at 2018/8/23
 */
class EditLayout(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    lateinit var type: SimpleEntry.EntryType
    private var mPictures: MutableList<TouchImageView> = mutableListOf()
    private var mEditText: EditText = EditText(context, null).apply {
        background = null
        width = screenWidth
        height = screenHeight
        hint = "写点什么想说的吧:)"
        gravity = Gravity.TOP
        setPadding(dp(15f).toInt(), dp(15f).toInt(), dp(15f).toInt(), dp(15f).toInt())
    }
    var data: Entry = Entry()
    private lateinit var viewModel: EntryViewModel

    init {
        addView(mEditText)
    }

    fun addViewModel(vm: EntryViewModel) {
        viewModel = vm
        if (type == SimpleEntry.EntryType.TODO) {

        }
    }

    fun loadData(dataEntry: Entry) {
        data = dataEntry
        type = data.simpleEntry.type
        if (type == SimpleEntry.EntryType.TODO) {

        } else {
            mEditText.setText(data.simpleEntry.detail)
            if (!data.imgList.isEmpty()) {
                data.imgList.forEach {
                    addView(TouchImageView(context, null).apply {
                        cWidth = it.width
                        cHeight = it.height
                        xLocation = it.xLocation
                        yLocation = it.yLocation
                        val option = RequestOptions.placeholderOf(ContextCompat.getDrawable(context, R.drawable.icon_place)).override(cWidth, cHeight).centerCrop()
                        Glide.with(context)
                                .asBitmap()
                                .load(it.uri)
                                .apply(option)
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        picturePro = resource.width / resource.height.toFloat()
                                        setImageBitmap(resource)
                                    }
                                })
                        mUri = it.uri
                        isClickable = true
                    }.also {
                        mPictures.add(it)
                    })
                    requestLayout()
                }
            }
        }
    }

    fun addPicture(urls: List<Uri>) {
        urls.forEach {
            addView(TouchImageView(context, null).apply {
                val option = RequestOptions.placeholderOf(ContextCompat.getDrawable(context, R.drawable.icon_place)).centerCrop()
                Glide.with(context)
                        .asBitmap()
                        .load(it)
                        .apply(option)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                picturePro = resource.width / resource.height.toFloat()
                                if (resource.width > screenWidth / 3){
                                    resource.width = screenWidth / 3
                                    resource.height = (screenWidth / 3 / picturePro).toInt()
                                }
                                if (resource.height > screenHeight / 4){
                                    resource.height = screenHeight / 4
                                    resource.width = (screenHeight / 4 * picturePro).toInt()
                                }
                                setImageBitmap(resource)
                            }
                        })
                mUri = getImageAbsolutePath(context, it).toString()
                isClickable = true
            }.also {
                mPictures.add(it)
            })
        }
    }

    fun saveData() = if (data.simpleEntry.id == 0) {
        Entry().apply {
            simpleEntry.title = mEditText.text.split("\n")[0]
            simpleEntry.detail = mEditText.text.toString()
            simpleEntry.type = type
            if (type != SimpleEntry.EntryType.TODO) {
                imgList = mPictures.asSequence().filter {
                    !it.isDelete
                }.map {
                    Image().apply {
                        this.xLocation = it.xLocation
                        this.yLocation = it.yLocation
                        this.width = it.cWidth
                        this.height = it.cHeight
                        this.uri = it.mUri
                    }
                }.toList()
            }
        }
    } else {
        data.apply {
            simpleEntry.title = mEditText.text.split("\n")[0]
            simpleEntry.detail = mEditText.text.toString()
            simpleEntry.type = type
            if (type != SimpleEntry.EntryType.TODO) {
                imgList = mPictures.asSequence().filter {
                    !it.isDelete
                }.map {
                    Image().apply {
                        this.xLocation = it.xLocation
                        this.yLocation = it.yLocation
                        this.width = it.cWidth
                        this.height = it.cHeight
                        this.uri = it.mUri
                    }
                }.toList()
            }
        }
    }

    fun changeType(type: SimpleEntry.EntryType) {
        this.type = type
        data.simpleEntry.type = type
        mEditText.setText("")
        mPictures.forEach {
            removeView(it)
        }
        mPictures.clear()
        if (type == SimpleEntry.EntryType.TODO){
            iv_edit_album.visibility = View.GONE
        } else {
            iv_edit_album.visibility = View.VISIBLE
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

}