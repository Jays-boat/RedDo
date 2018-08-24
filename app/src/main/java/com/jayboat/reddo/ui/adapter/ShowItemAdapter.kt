package com.jayboat.reddo.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jayboat.reddo.R
import com.jayboat.reddo.appContext
import com.jayboat.reddo.ui.viewholder.*

/*
 by Cynthia at 2018/8/21
 */
 class ShowItemAdapter(private var mData : List<String>, private var type : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val leftType = 1
    private val rightType = 2
    private val noneType = 0

    override fun getItemViewType(position: Int): Int {
        return when {
            itemCount - 1 == position -> noneType
            position % 2 == 0 -> rightType
            else -> leftType
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == leftType){
            return LeftItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_center_left,parent,false),type)
        } else if (viewType == rightType) {
            return RightItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_center_right,parent,false),type)
        } else {
            return NoneItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_none,parent,false),type)
        }
    }

    override fun getItemCount(): Int {
        return if (mData.isEmpty()) mData.size + 1 else  mData.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NoneItemViewHolder){
            holder.refreshData(type)
        } else if (holder is LeftItemViewHolder){

        } else {

        }
    }

    fun changeType(type: String){
        this.type = type
        notifyDataSetChanged()
    }
}

