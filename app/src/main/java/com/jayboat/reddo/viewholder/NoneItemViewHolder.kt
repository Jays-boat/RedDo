package com.jayboat.reddo.viewholder

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.jayboat.reddo.Const
import kotlinx.android.synthetic.main.recycle_item_none.view.*

/*
 by Cynthia at 2018/8/22
 */
class NoneItemViewHolder(itemView : View, type :String) : RecyclerView.ViewHolder(itemView){
    init {
        when(type){
            Const.TYPE_ALL -> itemView.tv_hint_none.setTextColor(Color.parseColor("#CCCCCC"))
            Const.TYPE_DAILY -> itemView.tv_hint_none.setTextColor(Color.parseColor("#F2CC63"))
            Const.TYPE_ESSAY -> itemView.tv_hint_none.setTextColor(Color.parseColor("#8CBAF0"))
            Const.TYPE_SCHEDULE -> itemView.tv_hint_none.setTextColor(Color.parseColor("#D68B85"))
            Const.TYPE_TODO -> itemView.tv_hint_none.setTextColor(Color.parseColor("#AED47A"))
        }
    }
}