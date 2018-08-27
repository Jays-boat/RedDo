package com.jayboat.reddo.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jayboat.reddo.R
import com.jayboat.reddo.room.bean.Entry
import com.jayboat.reddo.room.bean.SimpleEntry
import com.jayboat.reddo.ui.viewholder.FooterItemViewHolder
import com.jayboat.reddo.ui.viewholder.LeftItemViewHolder
import com.jayboat.reddo.ui.viewholder.NoneItemViewHolder
import com.jayboat.reddo.ui.viewholder.RightItemViewHolder
import com.jayboat.reddo.utils.screenHeight
import com.jayboat.reddo.viewmodel.EntryViewModel

/*
 by Cynthia at 2018/8/21
 */
 class ShowItemAdapter(private var mData : List<Entry>, private var type : String,private val vm:EntryViewModel,private var mClickListener:(id:Int,type:SimpleEntry.EntryType)->Unit,private var mLongClickListener:(data:Entry)->Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val leftType = 1
    private val rightType = 2
    private val noneType = 0
    private val footType = 3
    private var isEdit = false

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
            leftType -> LeftItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_center_left, parent, false))
            rightType -> RightItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_center_right, parent, false))
            noneType -> NoneItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_none, parent, false), type)
            else -> FooterItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycle_item_footer, parent, false))
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
                    initData(mData[position],isEdit,vm)
                    itemView.apply {
                        layoutParams.height = screenHeight / 8
                        setOnClickListener {
                            mClickListener(mData[position].simpleEntry.id,mData[position].simpleEntry.type)
                        }
                        setOnLongClickListener {
                            mLongClickListener(mData[position])
                            true
                        }
                    }
                }
            }
            is RightItemViewHolder -> {
                holder.apply {
                    initData(mData[position],isEdit,vm)
                    itemView.apply {
                        layoutParams.height = screenHeight / 8
                        setOnClickListener {
                            mClickListener(mData[position].simpleEntry.id,mData[position].simpleEntry.type)
                        }
                        setOnLongClickListener {
                            mLongClickListener(mData[position])
                            true
                        }
                    }
                }
            }
            else -> {
                (holder as FooterItemViewHolder).itemView.layoutParams.height = screenHeight / 10
            }
        }
    }

    fun changeType(type: String, data: List<Entry>) {
        this.type = type
        mData = data
        notifyDataSetChanged()
    }

    fun changeType(type: String) {
        this.type = type
        notifyDataSetChanged()
    }

    fun changeEdit(isEdit:Boolean){
        this.isEdit = isEdit
        notifyDataSetChanged()
    }

}

