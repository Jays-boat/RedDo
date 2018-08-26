package com.jayboat.reddo.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jayboat.reddo.R
import com.jayboat.reddo.appContext
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.ui.viewholder.*
import com.jayboat.reddo.utils.screenHeight

/*
 by Cynthia at 2018/8/21
 */
 class ShowItemAdapter(private var mData : List<Entry>, private var type : String,private var mListener:(id:Int)->Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val leftType = 1
    private val rightType = 2
    private val noneType = 0
    private val footType = 3

    override fun getItemViewType(position: Int): Int {
        return when {
            mData.isEmpty() -> noneType
            itemCount - 1 == position -> footType
            position % 2 == 0 -> rightType
            else -> leftType
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            leftType -> LeftItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_center_left,parent,false))
            rightType -> RightItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_center_right,parent,false))
            noneType -> NoneItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_none,parent,false),type)
            else -> FooterItemViewHolder(LayoutInflater.from(appContext).
                    inflate(R.layout.recycle_item_footer,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return mData.size + 1

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoneItemViewHolder -> holder.refreshData(type)
            is LeftItemViewHolder -> {
                holder.apply {
                    initData(mData[position])
                    itemView.apply {
                        layoutParams.height = screenHeight / 8
                        setOnClickListener {
                            mListener(mData[position].simpleEntry.id)
                        }
                    }
                }
            }
            is RightItemViewHolder -> {
                holder.apply {
                    initData(mData[position])
                    itemView.apply {
                        layoutParams.height = screenHeight / 8
                        setOnClickListener {
                            mListener(mData[position].simpleEntry.id)
                        }
                    }
                }
            }
            else -> {
                (holder as FooterItemViewHolder).itemView.layoutParams.height = screenHeight / 10
            }
        }
    }

    fun changeType(type: String,data:List<Entry>){
        this.type = type
        mData = data
        notifyDataSetChanged()
    }

    fun changeType(type:String){
        this.type = type
        notifyDataSetChanged()
    }
}

