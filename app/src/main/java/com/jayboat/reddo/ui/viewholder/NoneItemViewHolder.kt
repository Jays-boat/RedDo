package com.jayboat.reddo.ui.viewholder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jayboat.reddo.R
import com.jayboat.reddo.utils.*
import kotlinx.android.synthetic.main.recycle_item_none.view.*

/*
 by Cynthia at 2018/8/22
 */
class NoneItemViewHolder(itemView: View, type: String) : RecyclerView.ViewHolder(itemView) {

    private val info by lazy {
        mapOf(
                TYPE_ALL to (R.string.default_hint to Color.parseColor("#CCCCCC")),
                TYPE_DAILY to (R.string.default_hint to Color.parseColor("#F2CC63")),
                TYPE_ESSAY to (R.string.default_hint to Color.parseColor("#8CBAF0")),
                TYPE_AGENDA to (R.string.default_hint to Color.parseColor("#D68B85")),
                TYPE_TODO to (R.string.default_hint to Color.parseColor("#AED47A")),
                TYPE_SEARCH to (R.string.default_find to Color.parseColor("#CCCCCC"))
        )
    }

    init {
        refreshData(type)
    }

    fun refreshData(type: String) {
        itemView.tv_hint_none.apply {
            text = resources.getString(info[type]!!.first)
            setTextColor(info[type]!!.second)
        }
    }
}