package com.jayboat.reddo.ui.viewholder

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.jayboat.reddo.R
import com.jayboat.reddo.appContext
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.utils.getDataString
import kotlinx.android.synthetic.main.recycle_item_center_left.view.*

/*
 by Cynthia at 2018/8/22
 */
class LeftItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val shape by lazy {
        mapOf(
                SimpleEntry.EntryType.TODO to R.drawable.ic_item_todo,
                SimpleEntry.EntryType.ESSAY to R.drawable.ic_item_essay,
                SimpleEntry.EntryType.DAILY to R.drawable.ic_item_daily,
                SimpleEntry.EntryType.AGENDA to R.drawable.ic_item_agenda
        )
    }

    fun initData(data: Entry) {
        itemView.apply {
            tv_time_left.text = getDataString(data.simpleEntry.time)
            tv_right_title.text = data.simpleEntry.title
            iv_center_image.setImageDrawable(ContextCompat.getDrawable(appContext, shape[data.simpleEntry.type]!!))
            if (!data.imgList.isEmpty()) {
                Log.i("image", "${data.simpleEntry.id}:有图片！想不到吧！")
                Glide.with(appContext)
                        .load(data.imgList[0].uri)
                        .into(iv_right_image)
            } else {
                iv_right_image.visibility = View.GONE
            }
        }
    }
}