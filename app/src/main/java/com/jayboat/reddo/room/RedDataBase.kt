package com.jayboat.reddo.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jayboat.reddo.room.bean.*
import com.jayboat.reddo.room.dao.EntryDao
import com.jayboat.reddo.room.dao.ImageDao
import com.jayboat.reddo.room.dao.TextInfoDao
import com.jayboat.reddo.room.dao.TodoDao

@Database(entities = [SimpleEntry::class, TextInfo::class, Image::class, Todo::class], version = 1)
abstract class RedDataBase : RoomDatabase() {
    abstract fun getEntryDao(): EntryDao
    abstract fun getImageDao(): ImageDao
    abstract fun getTodoDao(): TodoDao
    abstract fun getTextInfoDao(): TextInfoDao

    companion object {
        const val NAME = "red_do_data_base"
    }
}